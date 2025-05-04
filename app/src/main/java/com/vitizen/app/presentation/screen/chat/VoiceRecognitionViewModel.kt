package com.vitizen.app.presentation.screen.chat

import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.VoiceRecognitionState
import com.vitizen.app.domain.repository.IVoiceRecognitionRepository
import com.vitizen.app.domain.usecase.StartVoiceRecognitionUseCase
import com.vitizen.app.domain.usecase.StopVoiceRecognitionUseCase
import com.vitizen.app.domain.usecase.TranscribeAudioUseCase
import com.vitizen.app.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceRecognitionViewModel @Inject constructor(
    private val startVoiceRecognition: StartVoiceRecognitionUseCase,
    private val stopVoiceRecognition: StopVoiceRecognitionUseCase,
    private val transcribeAudio: TranscribeAudioUseCase,
    private val repository: IVoiceRecognitionRepository
) : BaseViewModel<VoiceRecognitionState, VoiceRecognitionEvent>() {

    private val _state = MutableStateFlow(VoiceRecognitionState())
    val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()

    private var audioLevelJob: Job? = null

    fun startRecording() {
        viewModelScope.launch {
            try {
                startVoiceRecognition()
                _state.update { it.copy(isRecording = true, error = null) }
                setEvent(VoiceRecognitionEvent.RecordingStarted)
                startAudioLevelMonitoring()
            } catch (e: SecurityException) {
                _state.update { 
                    it.copy(
                        error = "Permission d'enregistrement nécessaire",
                        permissionGranted = false
                    )
                }
                setEvent(VoiceRecognitionEvent.Error("Permission d'enregistrement nécessaire"))
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isRecording = false,
                        error = "Erreur lors du démarrage de l'enregistrement"
                    )
                }
                setEvent(VoiceRecognitionEvent.Error("Erreur lors du démarrage de l'enregistrement"))
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isProcessing = true) }
                stopAudioLevelMonitoring()
                val audioData = stopVoiceRecognition()
                setEvent(VoiceRecognitionEvent.RecordingStopped)
                
                val transcribedText = transcribeAudio(audioData)
                _state.update { 
                    it.copy(
                        isRecording = false,
                        isProcessing = false,
                        transcribedText = transcribedText
                    )
                }
                setEvent(VoiceRecognitionEvent.TranscriptionComplete(transcribedText))
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isRecording = false,
                        isProcessing = false,
                        error = "Erreur lors de la transcription"
                    )
                }
                setEvent(VoiceRecognitionEvent.Error("Erreur lors de la transcription"))
            }
        }
    }

    private fun startAudioLevelMonitoring() {
        audioLevelJob?.cancel()
        audioLevelJob = viewModelScope.launch {
            while (true) {
                delay(100) // Mise à jour toutes les 100ms
                val level = getAudioLevel()
                _state.update { it.copy(audioLevel = level) }
                setEvent(VoiceRecognitionEvent.AudioLevelUpdated(level))
            }
        }
    }

    private fun stopAudioLevelMonitoring() {
        audioLevelJob?.cancel()
        audioLevelJob = null
    }

    private fun getAudioLevel(): Float {
        // Normaliser le niveau audio entre 0 et 1
        return try {
            val amplitude = repository.getAudioLevel()
            (amplitude / 32767f).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(permissionGranted = granted) }
        setEvent(VoiceRecognitionEvent.PermissionResult(granted))
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioLevelMonitoring()
        if (_state.value.isRecording) {
            viewModelScope.launch {
                try {
                    stopVoiceRecognition()
                } catch (e: Exception) {
                    // Ignorer les erreurs lors de la fermeture
                }
            }
        }
    }
} 