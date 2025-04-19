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
class ParametresViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ParametresUiState())
    val uiState: StateFlow<ParametresUiState> = _uiState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    fun startEditing() {
        _isEditing.value = true
    }

    fun saveParametres(
        domaine: String,
        fonction: String,
        surfaceBlanc: String,
        surfaceRouge: String,
        typeTraitement: String,
        pulverisateurs: List<PulverisateurInfo>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                domaine = domaine,
                fonction = fonction,
                surfaceBlanc = surfaceBlanc,
                surfaceRouge = surfaceRouge,
                typeTraitement = typeTraitement,
                pulverisateurs = pulverisateurs
            )
            _isEditing.value = false
        }
    }

    fun addPulverisateur(pulverisateur: PulverisateurInfo) {
        viewModelScope.launch {
            val currentList = _uiState.value.pulverisateurs.toMutableList()
            currentList.add(pulverisateur)
            _uiState.value = _uiState.value.copy(pulverisateurs = currentList)
        }
    }

    fun updatePulverisateur(oldNom: String, newPulverisateur: PulverisateurInfo) {
        viewModelScope.launch {
            val currentList = _uiState.value.pulverisateurs.toMutableList()
            val index = currentList.indexOfFirst { it.nom == oldNom }
            if (index != -1) {
                currentList[index] = newPulverisateur
                _uiState.value = _uiState.value.copy(pulverisateurs = currentList)
            }
        }
    }

    fun updatePulverisateur(index: Int, pulverisateur: PulverisateurInfo) {
        viewModelScope.launch {
            val currentList = _uiState.value.pulverisateurs.toMutableList()
            currentList[index] = pulverisateur
            _uiState.value = _uiState.value.copy(pulverisateurs = currentList)
        }
    }

    fun removePulverisateur(nom: String) {
        viewModelScope.launch {
            val currentList = _uiState.value.pulverisateurs.toMutableList()
            val index = currentList.indexOfFirst { it.nom == nom }
            if (index != -1) {
                currentList.removeAt(index)
                _uiState.value = _uiState.value.copy(pulverisateurs = currentList)
            }
        }
    }

    fun removePulverisateur(index: Int) {
        viewModelScope.launch {
            val currentList = _uiState.value.pulverisateurs.toMutableList()
            currentList.removeAt(index)
            _uiState.value = _uiState.value.copy(pulverisateurs = currentList)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Implémenter la déconnexion
        }
    }

    enum class TypePulverisateur {
        PORTE, TRAINE, AUTOMOTEUR, ENJAMBEUR
    }

    enum class TypePulverisation {
        JET_PROJETE, JET_HYDRAULIQUE, PNEUMATIQUE, ELECTROSTATIQUE, CONFINEE
    }

    enum class SystemeRinçage {
        OUI, NON
    }

    enum class SystemeGPS {
        AUCUN, GPS_UNIQUEMENT, GPS_CAPTEURS, DPAE
    }

    enum class TypeBuse {
        STANDARD, ANTIDERIVE, MIROIR, INJECTION_AIR, FENTE, CONIQUE_CREUX
    }

    enum class CodeCouleurISO {
        ROUGE, JAUNE, BLEU, VERT, ORANGE, MARRON, GRIS
    }

    data class PulverisateurInfo(
        val nom: String = "",
        val typePulverisateur: TypePulverisateur? = null,
        val typePulverisation: TypePulverisation? = null,
        val modeleMarque: String = "",
        val pression: String = "",
        val debit: String = "",
        val uniteDebit: UniteDebit = UniteDebit.L_MIN,
        val nombreRangs: String = "",
        val volumeBacPrincipal: String = "",
        val volumeBacSecondaire: String = "",
        val volumeBacRinçage: String = "",
        val largeurRampe: String = "",
        val surfaceMoyenne: String = "",
        val systemeRinçage: SystemeRinçage? = null,
        val systemeGPS: SystemeGPS? = null,
        val typeBuse: TypeBuse? = null,
        val nombreBuses: String = "",
        val anglePulverisation: String = "",
        val codeCouleurISO: CodeCouleurISO? = null
    )

    enum class UniteDebit {
        L_MIN, L_HA
    }

    data class ParametresUiState(
        val notificationsEnabled: Boolean = true,
        val domaine: String = "",
        val fonction: String = "",
        val surfaceBlanc: String = "",
        val surfaceRouge: String = "",
        val typeTraitement: String = "",
        val pulverisateurs: List<PulverisateurInfo> = emptyList()
    )
} 