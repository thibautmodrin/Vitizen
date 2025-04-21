package com.vitizen.app.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.dao.InformationsGeneralesDao
import com.vitizen.app.data.dao.OperateurDao
import com.vitizen.app.data.entity.InformationsGeneralesEntity
import com.vitizen.app.data.entity.OperateurEntity
import com.vitizen.app.data.entity.PulverisateurEntity
import com.vitizen.app.data.repository.InformationsGeneralesRepository
import com.vitizen.app.data.repository.PulverisateurRepository
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
    private val operateurDao: OperateurDao,
    private val pulverisateurRepository: PulverisateurRepository
) : ViewModel() {

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

    private val _pulverisateurs = MutableStateFlow<List<PulverisateurEntity>>(emptyList())
    val pulverisateurs: StateFlow<List<PulverisateurEntity>> = _pulverisateurs.asStateFlow()

    init {
        loadInformationsGenerales()
        viewModelScope.launch {
            operateurDao.getAll().collect { operateursList ->
                _operateurs.value = operateursList
            }
        }
        viewModelScope.launch {
            pulverisateurRepository.getAllPulverisateurs().collect { pulverisateurs ->
                _pulverisateurs.value = pulverisateurs
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



    fun deleteInformationsGenerales(informations: InformationsGeneralesEntity) {
        viewModelScope.launch {
            informationsGeneralesDao.delete(informations)
        }
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


    suspend fun getPulverisateurById(id: Long): PulverisateurEntity? {
        return pulverisateurRepository.getPulverisateurById(id)
    }

    suspend fun addPulverisateur(
        nomMateriel: String,
        modeDeplacement: String,
        nombreRampes: Int?,
        nombreBusesParRampe: Int?,
        typeBuses: String,
        pressionPulverisation: Float?,
        debitParBuse: Float?,
        anglePulverisation: Int?,
        largeurTraitement: Float?,
        plageVitesseAvancementMin: Float?,
        plageVitesseAvancementMax: Float?,
        volumeTotalCuve: Int?
    ) {
        val pulverisateur = PulverisateurEntity(
            nomMateriel = nomMateriel,
            modeDeplacement = modeDeplacement,
            nombreRampes = nombreRampes,
            nombreBusesParRampe = nombreBusesParRampe,
            typeBuses = typeBuses,
            pressionPulverisation = pressionPulverisation,
            debitParBuse = debitParBuse,
            anglePulverisation = anglePulverisation,
            largeurTraitement = largeurTraitement,
            plageVitesseAvancementMin = plageVitesseAvancementMin,
            plageVitesseAvancementMax = plageVitesseAvancementMax,
            volumeTotalCuve = volumeTotalCuve
        )
        pulverisateurRepository.addPulverisateur(pulverisateur)
    }

    suspend fun updatePulverisateur(
        id: Long,
        nomMateriel: String,
        modeDeplacement: String,
        nombreRampes: Int?,
        nombreBusesParRampe: Int?,
        typeBuses: String,
        pressionPulverisation: Float?,
        debitParBuse: Float?,
        anglePulverisation: Int?,
        largeurTraitement: Float?,
        plageVitesseAvancementMin: Float?,
        plageVitesseAvancementMax: Float?,
        volumeTotalCuve: Int?
    ) {
        val pulverisateur = PulverisateurEntity(
            id = id,
            nomMateriel = nomMateriel,
            modeDeplacement = modeDeplacement,
            nombreRampes = nombreRampes,
            nombreBusesParRampe = nombreBusesParRampe,
            typeBuses = typeBuses,
            pressionPulverisation = pressionPulverisation,
            debitParBuse = debitParBuse,
            anglePulverisation = anglePulverisation,
            largeurTraitement = largeurTraitement,
            plageVitesseAvancementMin = plageVitesseAvancementMin,
            plageVitesseAvancementMax = plageVitesseAvancementMax,
            volumeTotalCuve = volumeTotalCuve
        )
        pulverisateurRepository.updatePulverisateur(pulverisateur)
    }

    suspend fun deletePulverisateur(pulverisateur: PulverisateurEntity) {
        pulverisateurRepository.deletePulverisateur(pulverisateur)
    }
} 