package com.vitizen.app.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TreatmentViewModel @Inject constructor() : ViewModel() {
    private val _uiState = mutableStateOf(TreatmentUiState())
    val uiState: TreatmentUiState
        get() = _uiState.value

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    fun submitTreatment(
        date: String,
        type: String,
        produit: String,
        surface: String,
        commentaire: String
    ) {
        val errors = mutableMapOf<String, String>()
        
        if (date.isBlank()) {
            errors["date"] = "La date est requise"
        }
        if (type.isBlank()) {
            errors["type"] = "Le type de traitement est requis"
        }
        if (produit.isBlank()) {
            errors["produit"] = "Le produit est requis"
        }
        if (surface.isBlank()) {
            errors["surface"] = "La surface est requise"
        }

        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(fieldErrors = errors)
            return
        }

        viewModelScope.launch {
            try {
                // TODO: Implémenter la logique de sauvegarde du traitement
                _eventFlow.emit(UiEvent.ShowToast("Traitement enregistré avec succès"))
                _eventFlow.emit(UiEvent.NavigateBack)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Erreur lors de l'enregistrement du traitement"))
            }
        }
    }

    data class TreatmentUiState(
        val fieldErrors: Map<String, String> = emptyMap()
    )

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }
} 