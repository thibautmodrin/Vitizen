package com.vitizen.app.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.vitizen.app.data.remote.api.WhisperApi
import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@Suppress("DEPRECATION")
class VoiceRecognitionRepositoryImpl @Inject constructor(
    private val whisperApi: WhisperApi,
    @ApplicationContext private val context: Context
) : IVoiceRecognitionRepository {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    override suspend fun startRecording() {
        withContext(Dispatchers.IO) {
            try {
                audioFile = createAudioFile(context)
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(audioFile?.absolutePath)
                    setAudioEncodingBitRate(128000)
                    setAudioSamplingRate(16000)
                    prepare()
                    start()
                }
                isRecording = true
            } catch (e: Exception) {
                throw VoiceRecognitionException("Erreur lors du démarrage de l'enregistrement", e)
            }
        }
    }

    override suspend fun stopRecording(): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                isRecording = false
                audioFile?.readBytes() ?: throw VoiceRecognitionException("Fichier audio non trouvé")
            } catch (e: Exception) {
                throw VoiceRecognitionException("Erreur lors de l'arrêt de l'enregistrement", e)
            } finally {
                audioFile?.delete()
                audioFile = null
            }
        }
    }

    override suspend fun transcribeAudio(audioData: ByteArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val audioRequestBody = audioData.toRequestBody("audio/mpeg".toMediaTypeOrNull())
                val audioPart = MultipartBody.Part.createFormData("file", "audio.mp3", audioRequestBody)
                val modelPart = MultipartBody.Part.createFormData("model", "whisper-1")
                val languagePart = MultipartBody.Part.createFormData("language", "fr")
                val responseFormatPart = MultipartBody.Part.createFormData("response_format", "json")

                // Prompt contextuel pour la viticulture et les traitements phytosanitaires
                val promptText = """
                    Contexte viticole : Cette transcription concerne une application d'assistant pour viticulteurs.
                    Termes techniques courants : traitement phytosanitaire, pulvérisation, bouillie bordelaise, 
                    soufre, cuivre, mildiou, oïdium, botrytis, flavescence dorée, cicadelle, tordeuse, 
                    acariens, ravageurs, maladies cryptogamiques, produits phytosanitaires, 
                    dose d'application, volume de bouillie, stade phénologique, période de traitement, 
                    délai avant récolte, protection des cultures, lutte intégrée, agriculture raisonnée.
                    Noms de produits : bouillie bordelaise, soufre mouillable, cuivre, fongicides, 
                    insecticides, acaricides, produits de biocontrôle.
                    Unités de mesure : litres par hectare, kilogrammes par hectare, grammes par hectare, 
                    pourcentage de concentration, degrés Baumé.
                    Stades de développement : débourrement, feuillaison, floraison, nouaison, 
                    véraison, maturité, vendange.
                """.trimIndent()
                val promptPart = MultipartBody.Part.createFormData("prompt", promptText)

                println("Envoi de l'audio à l'API Whisper...")
                println("Taille de l'audio: ${audioData.size} bytes")
                val response = whisperApi.transcribe(
                    file = audioPart,
                    model = modelPart,
                    language = languagePart,
                    responseFormat = responseFormatPart,
                    prompt = promptPart
                )
                println("Réponse reçue de l'API Whisper: ${response.text}")
                response.text
            } catch (e: Exception) {
                println("Erreur lors de la transcription: ${e.message}")
                e.printStackTrace()
                throw VoiceRecognitionException("Erreur lors de la transcription", e)
            }
        }
    }

    override fun isRecording(): Boolean = isRecording

    override fun getAudioLevel(): Float {
        return if (isRecording && mediaRecorder != null) {
            try {
                mediaRecorder?.maxAmplitude?.toFloat() ?: 0f
            } catch (e: Exception) {
                0f
            }
        } else 0f
    }

    override suspend fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createAudioFile(context: Context): File {
        val timestamp = System.currentTimeMillis()
        val fileName = "audio_record_$timestamp.mp3"
        return File(context.cacheDir, fileName)
    }
}

class VoiceRecognitionException(message: String, cause: Throwable? = null) : Exception(message, cause) 