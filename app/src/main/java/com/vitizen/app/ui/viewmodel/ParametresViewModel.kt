package com.vitizen.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.dao.InformationsGeneralesDao
import com.vitizen.app.data.dao.OperateurDao
import com.vitizen.app.data.entity.InformationsGeneralesEntity
import com.vitizen.app.data.entity.OperateurEntity
import com.vitizen.app.data.repository.InformationsGeneralesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametresViewModel @Inject constructor(
    private val repository: InformationsGeneralesRepository,
    private val informationsGeneralesDao: InformationsGeneralesDao,
    private val operateurDao: OperateurDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParametresUiState())
    val uiState: StateFlow<ParametresUiState> = _uiState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _isEditingGeneralInfo = MutableStateFlow(false)
    val isEditingGeneralInfo: StateFlow<Boolean> = _isEditingGeneralInfo.asStateFlow()

    private val _isEditingOperator = MutableStateFlow(false)
    val isEditingOperator: StateFlow<Boolean> = _isEditingOperator.asStateFlow()

    private val _informationsGenerales = MutableStateFlow<List<InformationsGeneralesEntity>>(emptyList())
    val informationsGenerales: StateFlow<List<InformationsGeneralesEntity>> = _informationsGenerales.asStateFlow()

    private val _isGeneralInfoExpanded = MutableStateFlow(true)
    val isGeneralInfoExpanded: StateFlow<Boolean> = _isGeneralInfoExpanded.asStateFlow()

    private val _operateurs = MutableStateFlow<List<OperateurEntity>>(emptyList())
    val operateurs: StateFlow<List<OperateurEntity>> = _operateurs.asStateFlow()

    private val _isOperatorExpanded = MutableStateFlow(true)
    val isOperatorExpanded: StateFlow<Boolean> = _isOperatorExpanded.asStateFlow()

    init {
        loadInformationsGenerales()
        viewModelScope.launch {
            operateurDao.getAll().collect { operateursList ->
                _operateurs.value = operateursList
            }
        }
    }

    private fun loadInformationsGenerales() {
        viewModelScope.launch {
            repository.getAll().collect { informationsList ->
                _informationsGenerales.value = informationsList
            }
        }
    }

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

    fun addInformationsGenerales(
        nomDomaine: String,
        modeCulture: String,
        certifications: List<String>,
        surfaceTotale: Float,
        codePostal: String
    ) {
        viewModelScope.launch {
            val informations = InformationsGeneralesEntity(
                nomDomaine = nomDomaine,
                modeCulture = modeCulture,
                certifications = certifications,
                surfaceTotale = surfaceTotale,
                codePostal = codePostal
            )
            informationsGeneralesDao.insert(informations)
            _isGeneralInfoExpanded.value = true
        }
    }

    fun updateInformationsGenerales(informations: InformationsGeneralesEntity) {
        viewModelScope.launch {
            informationsGeneralesDao.update(informations)
        }
    }

    fun deleteInformationsGenerales(informations: InformationsGeneralesEntity) {
        viewModelScope.launch {
            informationsGeneralesDao.delete(informations)
        }
    }

    fun updateNomDomaine(nomDomaine: String) {
        _uiState.value = _uiState.value.copy(
            informationsGenerales = _uiState.value.informationsGenerales.copy(
                nomDomaine = nomDomaine
            )
        )
    }

    fun updateModeCulture(modeCulture: String) {
        _uiState.value = _uiState.value.copy(
            informationsGenerales = _uiState.value.informationsGenerales.copy(
                modeCulture = modeCulture
            )
        )
    }

    fun updateCertifications(certifications: List<String>) {
        _uiState.value = _uiState.value.copy(
            informationsGenerales = _uiState.value.informationsGenerales.copy(
                certifications = certifications
            )
        )
    }

    fun updateSurfaceTotale(surfaceTotale: Float?) {
        _uiState.value = _uiState.value.copy(
            informationsGenerales = _uiState.value.informationsGenerales.copy(
                surfaceTotale = surfaceTotale ?: 0f
            )
        )
    }

    fun updateCodePostal(codePostal: String) {
        _uiState.value = _uiState.value.copy(
            informationsGenerales = _uiState.value.informationsGenerales.copy(
                codePostal = codePostal
            )
        )
    }

    fun setGeneralInfoExpanded(expanded: Boolean) {
        _isGeneralInfoExpanded.value = expanded
    }

    fun setOperatorExpanded(expanded: Boolean) {
        _isOperatorExpanded.value = expanded
    }

    fun addOperateur(
        nom: String,
        disponibleWeekend: Boolean,
        diplomes: List<String>,
        materielMaitrise: List<String>
    ) {
        viewModelScope.launch {
            val operateur = OperateurEntity(
                nom = nom,
                disponibleWeekend = disponibleWeekend,
                diplomes = diplomes,
                materielMaitrise = materielMaitrise
            )
            operateurDao.insert(operateur)
            _isOperatorExpanded.value = true
            operateurDao.getAll().collect { operateursList ->
                _operateurs.value = operateursList
            }
        }
    }

    fun updateOperateur(operateur: OperateurEntity) {
        viewModelScope.launch {
            operateurDao.update(operateur)
        }
    }

    fun deleteOperateur(operateur: OperateurEntity) {
        viewModelScope.launch {
            operateurDao.delete(operateur)
        }
    }

    fun startEditingGeneralInfo() {
        _isEditingGeneralInfo.value = true
    }

    fun stopEditingGeneralInfo() {
        _isEditingGeneralInfo.value = false
    }

    fun startEditingOperator() {
        _isEditingOperator.value = true
    }

    fun stopEditingOperator() {
        _isEditingOperator.value = false
    }

    fun getOperateurById(id: Long): OperateurEntity? {
        return operateurs.value.find { it.id == id }
    }

    fun getInformationsGeneralesById(id: Long): InformationsGeneralesEntity? {
        return informationsGenerales.value.find { it.id == id }
    }

    fun updateOperateur(
        id: Long,
        nom: String,
        disponibleWeekend: Boolean,
        diplomes: List<String>,
        materielMaitrise: List<String>
    ) {
        viewModelScope.launch {
            val operateur = OperateurEntity(
                id = id,
                nom = nom,
                disponibleWeekend = disponibleWeekend,
                diplomes = diplomes,
                materielMaitrise = materielMaitrise
            )
            operateurDao.update(operateur)
        }
    }

    fun updateInformationsGenerales(
        id: Long,
        nomDomaine: String,
        modeCulture: String,
        surfaceTotale: Float,
        codePostal: String,
        certifications: List<String>
    ) {
        viewModelScope.launch {
            val informations = InformationsGeneralesEntity(
                id = id,
                nomDomaine = nomDomaine,
                modeCulture = modeCulture,
                surfaceTotale = surfaceTotale,
                codePostal = codePostal,
                certifications = certifications
            )
            informationsGeneralesDao.update(informations)
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
        val pulverisateurs: List<PulverisateurInfo> = emptyList(),
        val informationsGenerales: InformationsGeneralesEntity = InformationsGeneralesEntity(
            nomDomaine = "",
            modeCulture = "",
            certifications = emptyList(),
            surfaceTotale = 0f,
            codePostal = ""
        )
    )

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
} 