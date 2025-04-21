package com.vitizen.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.entity.TreatmentEntity
import com.vitizen.app.data.repository.TreatmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SuiviViewModel @Inject constructor(
    private val repository: TreatmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SuiviUiState())
    val uiState: StateFlow<SuiviUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    init {
        loadTreatments()
    }

    private fun loadTreatments() {
        viewModelScope.launch {
            repository.getAllTreatments().collect { treatments ->
                _uiState.value = SuiviUiState(
                    treatments = treatments.map { treatment ->
                        TreatmentCardState(
                            id = treatment.id,
                            date = treatment.startDate,
                            type = treatment.name,
                            product = treatment.description,
                            surface = treatment.doctor,
                            comment = treatment.notes ?: ""
                        )
                    }
                )
            }
        }
    }

    fun showDeleteConfirmation(treatmentId: Long) {
        _uiState.value = _uiState.value.copy(
            treatmentToDelete = treatmentId
        )
    }

    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(
            treatmentToDelete = null
        )
    }

    fun deleteTreatment(treatmentId: Long) {
        viewModelScope.launch {
            repository.getTreatmentById(treatmentId)?.let { treatment ->
                repository.deleteTreatment(treatment)
            }
        }
    }

    data class SuiviUiState(
        val treatments: List<TreatmentCardState> = emptyList(),
        val treatmentToDelete: Long? = null
    )

    data class TreatmentCardState(
        val id: Long,
        val date: String,
        val type: String,
        val product: String,
        val surface: String,
        val comment: String
    )
} 