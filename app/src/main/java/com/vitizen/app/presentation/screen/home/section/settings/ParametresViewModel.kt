package com.vitizen.app.presentation.screen.home.section.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import com.vitizen.app.data.local.entity.OperateurEntity
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.data.local.entity.PulverisateurEntity
import com.vitizen.app.domain.repository.IInformationsGeneralesRepository
import com.vitizen.app.domain.repository.IOperateurRepository
import com.vitizen.app.domain.repository.IParcelleRepository
import com.vitizen.app.domain.repository.IPulverisateurRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametresViewModel @Inject constructor(
    private val repository: IInformationsGeneralesRepository,
    private val informationsGeneralesDao: InformationsGeneralesDao,
    private val operateurRepository: IOperateurRepository,
    private val pulverisateurRepository: IPulverisateurRepository,
    private val parcelleRepository: IParcelleRepository
) : ViewModel() {

    private val _isEditingGeneralInfo = MutableStateFlow(false)
    val isEditingGeneralInfo: StateFlow<Boolean> = _isEditingGeneralInfo.asStateFlow()

    private val _isEditingOperator = MutableStateFlow(false)
    val isEditingOperator: StateFlow<Boolean> = _isEditingOperator.asStateFlow()

    private val _informationsGenerales =
        MutableStateFlow<List<InformationsGeneralesEntity>>(emptyList())
    val informationsGenerales: StateFlow<List<InformationsGeneralesEntity>> = _informationsGenerales.asStateFlow()

    private val _isGeneralInfoExpanded = MutableStateFlow(true)
    val isGeneralInfoExpanded: StateFlow<Boolean> = _isGeneralInfoExpanded.asStateFlow()

    private val _operateurs = MutableStateFlow<List<OperateurEntity>>(emptyList())
    val operateurs: StateFlow<List<OperateurEntity>> = _operateurs.asStateFlow()

    private val _isOperatorExpanded = MutableStateFlow(true)
    val isOperatorExpanded: StateFlow<Boolean> = _isOperatorExpanded.asStateFlow()

    private val _pulverisateurs = MutableStateFlow<List<PulverisateurEntity>>(emptyList())
    val pulverisateurs: StateFlow<List<PulverisateurEntity>> = _pulverisateurs.asStateFlow()

    private val _parcelles = MutableStateFlow<List<ParcelleEntity>>(emptyList())
    val parcelles: StateFlow<List<ParcelleEntity>> = _parcelles.asStateFlow()

    init {
        loadInformationsGenerales()
        viewModelScope.launch {
            operateurRepository.getAll().collect { operateursList ->
                _operateurs.value = operateursList
            }
        }
        viewModelScope.launch {
            pulverisateurRepository.getAllPulverisateurs().collect { pulverisateurs ->
                _pulverisateurs.value = pulverisateurs
            }
        }
        viewModelScope.launch {
            parcelleRepository.getAllParcelles().collect { parcelles ->
                _parcelles.value = parcelles
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
            operateurRepository.insert(operateur)
            _isOperatorExpanded.value = true
        }
    }

    fun deleteOperateur(operateur: OperateurEntity) {
        viewModelScope.launch {
            operateurRepository.delete(operateur)
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
            operateurRepository.update(operateur)
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

    suspend fun addParcelle(
        nom: String,
        surface: Float,
        cepage: String,
        anneePlantation: Int,
        typeConduite: String,
        largeurInterrang: Float?,
        hauteurFeuillage: Float?,
        accessibleMateriel: List<String>,
        zoneSensible: Boolean,
        zoneHumide: Boolean,
        drainage: Boolean,
        enherbement: Boolean,
        pente: String,
        typeSol: String,
        inondable: Boolean,
        latitude: Double?,
        longitude: Double?
    ) {
        val parcelle = ParcelleEntity(
            nom = nom,
            surface = surface,
            cepage = cepage,
            anneePlantation = anneePlantation,
            typeConduite = typeConduite,
            largeurInterrang = largeurInterrang,
            hauteurFeuillage = hauteurFeuillage,
            accessibleMateriel = accessibleMateriel,
            zoneSensible = zoneSensible,
            zoneHumide = zoneHumide,
            drainage = drainage,
            enherbement = enherbement,
            pente = pente,
            typeSol = typeSol,
            inondable = inondable,
            latitude = latitude,
            longitude = longitude
        )
        parcelleRepository.addParcelle(parcelle)
    }

    suspend fun updateParcelle(
        id: Long,
        nom: String,
        surface: Float,
        cepage: String,
        anneePlantation: Int,
        typeConduite: String,
        largeurInterrang: Float?,
        hauteurFeuillage: Float?,
        accessibleMateriel: List<String>,
        zoneSensible: Boolean,
        zoneHumide: Boolean,
        drainage: Boolean,
        enherbement: Boolean,
        pente: String,
        typeSol: String,
        inondable: Boolean,
        latitude: Double?,
        longitude: Double?
    ) {
        val parcelle = ParcelleEntity(
            id = id,
            nom = nom,
            surface = surface,
            cepage = cepage,
            anneePlantation = anneePlantation,
            typeConduite = typeConduite,
            largeurInterrang = largeurInterrang,
            hauteurFeuillage = hauteurFeuillage,
            accessibleMateriel = accessibleMateriel,
            zoneSensible = zoneSensible,
            zoneHumide = zoneHumide,
            drainage = drainage,
            enherbement = enherbement,
            pente = pente,
            typeSol = typeSol,
            inondable = inondable,
            latitude = latitude,
            longitude = longitude
        )
        parcelleRepository.updateParcelle(parcelle)
    }

    suspend fun deleteParcelle(parcelle: ParcelleEntity) {
        parcelleRepository.deleteParcelle(parcelle)
    }

    suspend fun getParcelleById(id: Long): ParcelleEntity? {
        return parcelleRepository.getParcelleById(id)
    }
}