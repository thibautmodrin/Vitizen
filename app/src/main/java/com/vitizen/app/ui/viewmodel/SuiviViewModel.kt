package com.vitizen.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuiviViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SuiviUiState())
    val uiState: StateFlow<SuiviUiState> = _uiState.asStateFlow()

    data class SuiviUiState(
        val suivis: List<Suivi> = emptyList()
    )

    data class Suivi(
        val id: String,
        val date: String,
        val description: String
    )
} 