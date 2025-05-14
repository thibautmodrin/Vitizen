package com.vitizen.app.presentation.screen.home.section.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.InformationsGenerales
import com.vitizen.app.domain.model.Operateur
import com.vitizen.app.domain.model.Parcelle
import com.vitizen.app.domain.model.Pulverisateur
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
    private val operateurRepository: IOperateurRepository,
    private val pulverisateurRepository: IPulverisateurRepository,
    private val parcelleRepository: IParcelleRepository
) : ViewModel() {

    private val _informationsGenerales = MutableStateFlow<List<InformationsGenerales>>(emptyList())
    val informationsGenerales: StateFlow<List<InformationsGenerales>> = _informationsGenerales.asStateFlow()

    private val _operateurs = MutableStateFlow<List<Operateur>>(emptyList())
    val operateurs: StateFlow<List<Operateur>> = _operateurs.asStateFlow()

    private val _pulverisateurs = MutableStateFlow<List<Pulverisateur>>(emptyList())
    val pulverisateurs: StateFlow<List<Pulverisateur>> = _pulverisateurs.asStateFlow()

    private val _parcelles = MutableStateFlow<List<Parcelle>>(emptyList())
    val parcelles: StateFlow<List<Parcelle>> = _parcelles.asStateFlow()

    init {
        loadInformationsGenerales()
        viewModelScope.launch {
            operateurRepository.getAllOperateurs().collect { operateursList ->
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
            val informations = InformationsGenerales(
                nomDomaine = nomDomaine,
                modeCulture = modeCulture,
                certifications = certifications,
                surfaceTotale = surfaceTotale,
                codePostal = codePostal
            )
            repository.insert(informations)
        }
    }

    fun deleteInformationsGenerales(informations: InformationsGenerales) {
        viewModelScope.launch {
            repository.delete(informations)
        }
    }

    fun addOperateur(
        nom: String,
        disponibleWeekend: Boolean,
        diplomes: List<String>,
        materielMaitrise: List<String>
    ) {
        viewModelScope.launch {
            val operateur = Operateur(
                nom = nom,
                disponibleWeekend = disponibleWeekend,
                diplomes = diplomes,
                materielMaitrise = materielMaitrise
            )
            operateurRepository.addOperateur(operateur)
        }
    }

    fun deleteOperateur(operateur: Operateur) {
        viewModelScope.launch {
            operateurRepository.deleteOperateur(operateur)
        }
    }

    fun getOperateurById(id: Long): Operateur? {
        return operateurs.value.find { it.id == id }
    }

    fun getInformationsGeneralesById(id: Long): InformationsGenerales? {
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
            val operateur = Operateur(
                id = id,
                nom = nom,
                disponibleWeekend = disponibleWeekend,
                diplomes = diplomes,
                materielMaitrise = materielMaitrise
            )
            operateurRepository.updateOperateur(operateur)
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
            val informations = InformationsGenerales(
                id = id,
                nomDomaine = nomDomaine,
                modeCulture = modeCulture,
                surfaceTotale = surfaceTotale,
                codePostal = codePostal,
                certifications = certifications
            )
            repository.update(informations)
        }
    }

    suspend fun getPulverisateurById(id: Long): Pulverisateur? {
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
        val pulverisateur = Pulverisateur(
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
        val pulverisateur = Pulverisateur(
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

    suspend fun deletePulverisateur(pulverisateur: Pulverisateur) {
        pulverisateurRepository.deletePulverisateur(pulverisateur)
    }

    // Fonctions pour la gestion des parcelles
    fun addParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                parcelleRepository.addParcelle(parcelle)
            } catch (e: Exception) {
                // Gérer l'erreur si nécessaire
                Log.e("ParametresViewModel", "Erreur lors de l'ajout de la parcelle", e)
            }
        }
    }

    fun updateParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                parcelleRepository.updateParcelle(parcelle)
            } catch (e: Exception) {
                // Gérer l'erreur si nécessaire
                Log.e("ParametresViewModel", "Erreur lors de la mise à jour de la parcelle", e)
            }
        }
    }

    fun deleteParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                parcelleRepository.deleteParcelle(parcelle)
            } catch (e: Exception) {
                // Gérer l'erreur si nécessaire
                Log.e("ParametresViewModel", "Erreur lors de la suppression de la parcelle", e)
            }
        }
    }

    suspend fun getParcelleById(id: String): Parcelle? {
        return parcelleRepository.getParcelleById(id)
    }

    // Fonction pour obtenir une parcelle de manière asynchrone
    fun getParcelleByIdAsync(id: String, onResult: (Parcelle?) -> Unit) {
        viewModelScope.launch {
            try {
                val parcelle = getParcelleById(id)
                onResult(parcelle)
            } catch (e: Exception) {
                Log.e("ParametresViewModel", "Erreur lors de la récupération de la parcelle", e)
                onResult(null)
            }
        }
    }
}