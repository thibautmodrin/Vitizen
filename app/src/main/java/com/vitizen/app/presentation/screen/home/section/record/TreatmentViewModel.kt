package com.vitizen.app.presentation.screen.home.section.record

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.local.entity.TreatmentEntity
import com.vitizen.app.data.repository.TreatmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TreatmentViewModel @Inject constructor(
    private val repository: TreatmentRepository
) : ViewModel() {
    private val _uiState = mutableStateOf(TreatmentUiState())
    val uiState: TreatmentUiState
        get() = _uiState.value

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    init {
        resetForm()
    }

    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun updateType(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateProduit(produit: String) {
        _uiState.value = _uiState.value.copy(produit = produit)
    }

    fun updateSurface(surface: String) {
        _uiState.value = _uiState.value.copy(surface = surface)
    }

    fun updateCommentaire(commentaire: String) {
        _uiState.value = _uiState.value.copy(commentaire = commentaire)
    }

    private fun resetForm() {
        _uiState.value = TreatmentUiState(
            date = dateFormat.format(Date()),
            type = "",
            produit = "",
            surface = "",
            commentaire = "",
            fieldErrors = emptyMap()
        )
    }

    fun submitTreatment() {
        val errors = mutableMapOf<String, String>()

        with(_uiState.value) {
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
                    val treatment = TreatmentEntity(
                        name = type,
                        description = produit,
                        startDate = date,
                        endDate = null,
                        doctor = surface,
                        notes = commentaire.takeIf { it.isNotBlank() },
                        isCompleted = false
                    )

                    repository.insertTreatment(treatment)
                    resetForm()
                    _eventFlow.emit(UiEvent.ShowToast("Traitement enregistré avec succès"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                } catch (e: Exception) {
                    _eventFlow.emit(UiEvent.ShowToast("Erreur lors de l'enregistrement du traitement"))
                }
            }
        }
    }

    data class TreatmentUiState(
        val date: String = "",
        val type: String = "",
        val produit: String = "",
        val surface: String = "",
        val commentaire: String = "",
        val fieldErrors: Map<String, String> = emptyMap()
    )

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }
}