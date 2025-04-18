package com.vitizen.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event> : ViewModel() {
    private val _uiState = MutableStateFlow<State?>(null)
    val uiState: StateFlow<State?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _uiEvent = MutableStateFlow<Event?>(null)
    val uiEvent: StateFlow<Event?> = _uiEvent.asStateFlow()

    protected fun setState(state: State) {
        viewModelScope.launch {
            _uiState.value = state
        }
    }

    protected fun setLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _isLoading.value = isLoading
        }
    }

    protected fun setError(error: String?) {
        viewModelScope.launch {
            _error.value = error
        }
    }

    protected fun setEvent(event: Event) {
        viewModelScope.launch {
            _uiEvent.value = event
        }
    }

    fun clearError() {
        viewModelScope.launch {
            _error.value = null
        }
    }

    fun clearEvent() {
        viewModelScope.launch {
            _uiEvent.value = null
        }
    }
} 