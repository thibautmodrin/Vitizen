package com.vitizen.app.data.remote.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.File
import java.io.IOException

class VoskSpeechRecognizer(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : RecognitionListener {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null
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

    suspend fun initialize() {
        try {
            val modelDir = File(context.getExternalFilesDir(null), "model")
            if (!modelDir.exists()) {
                Log.d("Vosk", "📦 Décompression du modèle Vosk...")
                StorageService.unpack(context, "vosk-model-small-fr-0.22", "model", { model: Model ->
                    this.model = model
                    recognizer = Recognizer(model, sampleRate.toFloat())
                    Log.d("Vosk", "✅ Modèle Vosk initialisé avec succès")
                }, { e: IOException ->
                    Log.e("Vosk", "❌ Erreur lors de l'initialisation du modèle", e)
                })
            } else {
                Log.d("Vosk", "📂 Utilisation du modèle Vosk existant")
                model = Model(modelDir.absolutePath)
                recognizer = Recognizer(model, sampleRate.toFloat())
                Log.d("Vosk", "✅ Modèle Vosk initialisé avec succès")
            }
        } catch (e: Exception) {
            Log.e("Vosk", "❌ Erreur lors de l'initialisation", e)
        }
    }

    suspend fun startRecognition() {
        if (isRecording) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord?.startRecording()
            isRecording = true
            _isListening.emit(true)

            coroutineScope.launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize)
                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        recognizer?.acceptWaveForm(buffer, read)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Vosk", "❌ Erreur lors du démarrage de la reconnaissance", e)
            stopRecognition()
        }
    }

    suspend fun stopRecognition() {
        if (!isRecording) return

        try {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            _isListening.emit(false)
        } catch (e: Exception) {
            Log.e("Vosk", "❌ Erreur lors de l'arrêt de la reconnaissance", e)
        }
    }

    suspend fun release() {
        stopRecognition()
        recognizer?.close()
        model?.close()
        recognizer = null
        model = null
    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {
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
} 