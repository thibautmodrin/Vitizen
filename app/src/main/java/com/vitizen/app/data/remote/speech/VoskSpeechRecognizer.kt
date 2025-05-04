package com.vitizen.app.data.remote.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class VoskSpeechRecognizer(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : RecognitionListener {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private val _recognitionResults = MutableSharedFlow<SpeechRecognitionResult>()
    val recognitionResults: SharedFlow<SpeechRecognitionResult> = _recognitionResults.asSharedFlow()

    private val _isListening = MutableSharedFlow<Boolean>()
    val isListening: SharedFlow<Boolean> = _isListening.asSharedFlow()

    init {
        // Configuration du niveau de log
        LibVosk.setLogLevel(LogLevel.INFO)
    }

    private fun copyAssetsRecursively(context: Context, sourcePath: String, destDir: File) {
        context.assets.list(sourcePath)?.forEach { fileName ->
            val sourceFile = "$sourcePath/$fileName"
            val destFile = File(destDir, fileName)
            
            if (context.assets.list(sourceFile) != null) {
                // C'est un dossier, le créer
                destFile.mkdirs()
                // Copier récursivement le contenu
                copyAssetsRecursively(context, sourceFile, destFile)
            } else {
                // C'est un fichier, le copier
                try {
                    Log.d("VoskSpeechRecognizer", "📝 Copie du fichier: $sourceFile")
                    context.assets.open(sourceFile).use { input ->
                        val fileSize = input.available().toLong()
                        Log.d("VoskSpeechRecognizer", "📊 Taille du fichier source: $fileSize octets")
                        
                        // Vérifier si le fichier de destination existe déjà
                        if (destFile.exists()) {
                            Log.d("VoskSpeechRecognizer", "⚠️ Le fichier existe déjà, suppression: ${destFile.absolutePath}")
                            destFile.delete()
                        }
                        
                        destFile.outputStream().use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            var totalBytesRead = 0L
                            
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                                
                                // Log tous les 1MB copiés
                                if (totalBytesRead % (1024 * 1024) == 0L) {
                                    Log.d("VoskSpeechRecognizer", "📊 Progression: ${totalBytesRead / (1024 * 1024)}MB copiés")
                                }
                            }
                            
                            // Forcer l'écriture sur le disque
                            output.flush()
                            
                            // Vérifier la taille du fichier après la copie
                            val destFileSize = destFile.length()
                            Log.d("VoskSpeechRecognizer", "📊 Taille du fichier destination: $destFileSize octets")
                            
                            if (destFileSize != totalBytesRead) {
                                throw IOException("Erreur lors de la copie du fichier $sourceFile: taille incorrecte (source: $fileSize, copié: $totalBytesRead, destination: $destFileSize)")
                            }
                            
                            if (destFileSize != fileSize) {
                                throw IOException("Erreur lors de la copie du fichier $sourceFile: taille différente de la source (source: $fileSize, destination: $destFileSize)")
                            }
                            
                            Log.d("VoskSpeechRecognizer", "✅ Fichier copié avec succès: $sourceFile (${totalBytesRead} octets)")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("VoskSpeechRecognizer", "❌ Erreur lors de la copie du fichier $sourceFile", e)
                    throw e
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun initialize() {
        try {
            Log.d("VoskSpeechRecognizer", "📦 Initialisation du modèle Vosk...")
            
            // Vérifier si le modèle est présent dans les assets
            try {
                val modelFiles = context.assets.list("vosk-model-small-fr-0.22")
                if (modelFiles.isNullOrEmpty()) {
                    throw IOException("Le modèle Vosk n'est pas présent dans les assets")
                }
                Log.d("VoskSpeechRecognizer", "📦 Contenu du dossier vosk-model-small-fr-0.22: ${modelFiles.joinToString()}")
                
                // Vérifier les fichiers requis
                val requiredFiles = listOf(
                    "am/final.mdl",
                    "conf/model.conf",
                    "conf/mfcc.conf",
                    "graph/HCLr.fst",
                    "graph/Gr.fst",
                    "graph/disambig_tid.int",
                    "graph/phones/word_boundary.int"
                )
                
                // Vérifier que tous les fichiers requis sont présents
                requiredFiles.forEach { file ->
                    try {
                        context.assets.open("vosk-model-small-fr-0.22/$file").use { input ->
                            val size = input.available().toLong()
                            Log.d("VoskSpeechRecognizer", "📊 Taille du fichier vosk-model-small-fr-0.22/$file: $size octets")
                        }
                    } catch (e: Exception) {
                        throw IOException("Fichier manquant ou inaccessible: vosk-model-small-fr-0.22/$file")
                    }
                }
            } catch (e: Exception) {
                Log.e("VoskSpeechRecognizer", "❌ Erreur lors de la vérification des assets", e)
                throw e
            }

            // Initialiser le modèle via StorageService
            suspendCancellableCoroutine<Int> { continuation ->
                try {
                    // Utiliser StorageService pour extraire le modèle
                    StorageService.unpack(context, "vosk-model-small-fr-0.22", "model",
                        { model ->
                            try {
                                this@VoskSpeechRecognizer.model = model
                                recognizer = Recognizer(model, sampleRate.toFloat())
                                Log.d("VoskSpeechRecognizer", "✅ Modèle Vosk initialisé avec succès")
                                Log.d("VoskSpeechRecognizer", "Configuration du recognizer: sampleRate=$sampleRate")
                                continuation.resume(0) { }
                            } catch (e: Exception) {
                                Log.e("VoskSpeechRecognizer", "❌ Erreur lors de l'initialisation du modèle", e)
                                continuation.resumeWithException(e)
                            }
                        },
                        { exception ->
                            Log.e("VoskSpeechRecognizer", "❌ Erreur lors de l'extraction du modèle", exception)
                            continuation.resumeWithException(exception ?: IOException("Erreur inconnue lors de l'extraction du modèle"))
                        }
                    )
                } catch (e: Exception) {
                    Log.e("VoskSpeechRecognizer", "❌ Erreur lors de l'initialisation", e)
                    continuation.resumeWithException(e)
                }
            }
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors de l'initialisation", e)
            throw e
        }
    }

    suspend fun startRecognition() {
        if (isRecording) {
            Log.d("VoskSpeechRecognizer", "⚠️ La reconnaissance est déjà en cours")
            return
        }

        try {
            Log.d("VoskSpeechRecognizer", "🎤 Démarrage de la reconnaissance vocale")
            Log.d("VoskSpeechRecognizer", "📊 État du modèle: ${model != null}")
            
            if (model == null) {
                Log.e("VoskSpeechRecognizer", "❌ Le modèle n'est pas initialisé")
                return
            }

            try {
                val rec = Recognizer(model, sampleRate.toFloat())
                speechService = SpeechService(rec, sampleRate.toFloat())
                speechService?.startListening(this)
                isRecording = true
                _isListening.emit(true)
                Log.d("VoskSpeechRecognizer", "✅ Reconnaissance vocale démarrée avec succès")
            } catch (e: IOException) {
                Log.e("VoskSpeechRecognizer", "❌ Erreur lors du démarrage de la reconnaissance", e)
                stopRecognition()
            }
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors du démarrage de la reconnaissance", e)
            stopRecognition()
        }
    }

    suspend fun stopRecognition() {
        if (!isRecording) return

        try {
            isRecording = false
            speechService?.stop()
            speechService?.shutdown()
            speechService = null
            _isListening.emit(false)
            Log.d("VoskSpeechRecognizer", "✅ Reconnaissance vocale arrêtée")
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors de l'arrêt de la reconnaissance", e)
        }
    }

    suspend fun release() {
        stopRecognition()
        model?.close()
        model = null
    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {
            Log.d("VoskSpeechRecognizer", "Résultat partiel: $it")
            coroutineScope.launch {
                _recognitionResults.emit(
                    SpeechRecognitionResult(
                        text = it,
                        isFinal = false,
                        confidence = 0.0f
                    )
                )
            }
        }
    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {
            Log.d("VoskSpeechRecognizer", "Résultat final: $it")
            coroutineScope.launch {
                _recognitionResults.emit(
                    SpeechRecognitionResult(
                        text = it,
                        isFinal = true,
                        confidence = 1.0f
                    )
                )
            }
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        hypothesis?.let {
            Log.d("VoskSpeechRecognizer", "Résultat final confirmé: $it")
            coroutineScope.launch {
                _recognitionResults.emit(
                    SpeechRecognitionResult(
                        text = it,
                        isFinal = true,
                        confidence = 1.0f
                    )
                )
            }
        }
    }

    override fun onError(exception: Exception?) {
        exception?.let {
            Log.e("VoskSpeechRecognizer", "Erreur de reconnaissance: ${it.message}")
            coroutineScope.launch {
                _recognitionResults.emit(
                    SpeechRecognitionResult(
                        text = "",
                        isFinal = true,
                        confidence = 0.0f,
                        error = it.message
                    )
                )
            }
        }
    }

    override fun onTimeout() {
        Log.d("VoskSpeechRecognizer", "Timeout de la reconnaissance")
        coroutineScope.launch {
            _recognitionResults.emit(
                SpeechRecognitionResult(
                    text = "",
                    isFinal = true,
                    confidence = 0.0f,
                    error = "Timeout"
                )
            )
        }
    }

    suspend fun recognizeFromFile(inputStream: InputStream) {
        try {
            Log.d("VoskSpeechRecognizer", "🎵 Démarrage de la reconnaissance depuis un fichier")
            
            if (model == null) {
                Log.e("VoskSpeechRecognizer", "❌ Le modèle n'est pas initialisé")
                return
            }

            try {
                // Créer un nouveau recognizer avec le modèle
                val rec = Recognizer(model, sampleRate.toFloat())
                
                // Sauter l'en-tête WAV (44 octets)
                if (inputStream.skip(44) != 44L) {
                    throw IOException("Fichier trop court")
                }

                // Créer et démarrer le service de reconnaissance de flux
                speechStreamService = SpeechStreamService(rec, inputStream, sampleRate.toFloat())
                speechStreamService?.start(this)
                _isListening.emit(true)
                Log.d("VoskSpeechRecognizer", "✅ Reconnaissance depuis fichier démarrée avec succès")
            } catch (e: IOException) {
                Log.e("VoskSpeechRecognizer", "❌ Erreur lors du démarrage de la reconnaissance depuis fichier", e)
                stopRecognition()
            }
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors du démarrage de la reconnaissance depuis fichier", e)
            stopRecognition()
        }
    }

    suspend fun pauseRecognition(pause: Boolean) {
        try {
            speechService?.setPause(pause)
            Log.d("VoskSpeechRecognizer", if (pause) "⏸️ Reconnaissance en pause" else "▶️ Reconnaissance reprise")
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors de la mise en pause de la reconnaissance", e)
        }
    }

    fun destroy() {
        try {
            if (speechService != null) {
                speechService?.stop()
                speechService?.shutdown()
                speechService = null
            }

            if (speechStreamService != null) {
                speechStreamService?.stop()
                speechStreamService = null
            }

            model?.close()
            model = null
            Log.d("VoskSpeechRecognizer", "✅ Ressources libérées avec succès")
        } catch (e: Exception) {
            Log.e("VoskSpeechRecognizer", "❌ Erreur lors de la libération des ressources", e)
        }
    }
} 