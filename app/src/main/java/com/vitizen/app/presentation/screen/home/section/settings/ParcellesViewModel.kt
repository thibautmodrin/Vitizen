package com.vitizen.app.presentation.screen.home.section.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitizen.app.domain.model.Parcelle
import com.vitizen.app.domain.repository.IParcelleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class ParcellesViewModel @Inject constructor(
    private val parcelleRepository: IParcelleRepository
) : ViewModel() {

    // Coordonnées de Beaune
    private val BEAUNE_LATITUDE = 47.0242
    private val BEAUNE_LONGITUDE = 4.8386
    private val DEFAULT_ZOOM = 15.0

    // États pour les parcelles
    private val _parcelles = MutableStateFlow<List<Parcelle>>(emptyList())
    val parcelles: StateFlow<List<Parcelle>> = _parcelles.asStateFlow()

    private val _selectedParcelle = MutableStateFlow<Parcelle?>(null)
    val selectedParcelle: StateFlow<Parcelle?> = _selectedParcelle.asStateFlow()

    private val _parcelleToDelete = MutableStateFlow<Parcelle?>(null)
    val parcelleToDelete: StateFlow<Parcelle?> = _parcelleToDelete.asStateFlow()

    // États pour la carte
    private val _mapCenter = MutableStateFlow(GeoPoint(BEAUNE_LATITUDE, BEAUNE_LONGITUDE))
    val mapCenter: StateFlow<GeoPoint> = _mapCenter.asStateFlow()

    private val _mapZoom = MutableStateFlow(DEFAULT_ZOOM)
    val mapZoom: StateFlow<Double> = _mapZoom.asStateFlow()

    // États pour les modes d'édition
    private val _isPointMode = MutableStateFlow(false)
    val isPointMode: StateFlow<Boolean> = _isPointMode.asStateFlow()

    private val _isPolygonMode = MutableStateFlow(false)
    val isPolygonMode: StateFlow<Boolean> = _isPolygonMode.asStateFlow()
    
    // État pour les marqueurs
    private val _markerPosition = MutableStateFlow<GeoPoint?>(null)
    val markerPosition: StateFlow<GeoPoint?> = _markerPosition.asStateFlow()
    
    // État pour les marqueurs permanents associés aux parcelles
    private val _permanentMarkers = MutableStateFlow<Map<String, GeoPoint>>(emptyMap())
    val permanentMarkers: StateFlow<Map<String, GeoPoint>> = _permanentMarkers.asStateFlow()
    
    // État pour indiquer si les marqueurs doivent être retirés
    private val _shouldClearMarkers = MutableStateFlow(false)
    val shouldClearMarkers: StateFlow<Boolean> = _shouldClearMarkers.asStateFlow()

    init {
        Log.d("ParcellesViewModel", "Initialisation du ViewModel")
        loadParcelles()
        observeParcelleToDelete()
        centerMapOnBeaune()
    }

    // Gestion des modes d'édition
    fun togglePointMode() {
        val newPointModeValue = !_isPointMode.value
        
        // Si on active le mode point, on désactive le mode polygon
        if (newPointModeValue) {
            _isPolygonMode.value = false
        }
        
        _isPointMode.value = newPointModeValue
        
        // Réinitialiser les marqueurs lors du changement de mode
        clearMarkers()
        
        Log.d("ParcellesViewModel", "Mode point unique: ${_isPointMode.value}")
    }
    
    fun togglePolygonMode() {
        val newPolygonModeValue = !_isPolygonMode.value
        
        // Si on active le mode polygon, on désactive le mode point
        if (newPolygonModeValue) {
            _isPointMode.value = false
        }
        
        _isPolygonMode.value = newPolygonModeValue
        
        // Réinitialiser les marqueurs lors du changement de mode
        clearMarkers()
        
        Log.d("ParcellesViewModel", "Mode polygon: ${_isPolygonMode.value}")
    }

    // Gestion des marqueurs
    fun setMarkerPosition(position: GeoPoint) {
        _markerPosition.value = position
        _shouldClearMarkers.value = true
        Log.d("ParcellesViewModel", "Marqueur positionné à: ${position.latitude}, ${position.longitude}")
    }
    
    fun clearMarkers() {
        _markerPosition.value = null
        _shouldClearMarkers.value = true
    }
    
    fun markersCleared() {
        _shouldClearMarkers.value = false
    }
    
    // Désactive le mode point et conserve le marqueur
    fun finishPointMode(parcelleId: String, position: GeoPoint) {
        // Désactiver le mode point
        _isPointMode.value = false
        
        // Ajouter le marqueur aux marqueurs permanents
        val updatedMarkers = _permanentMarkers.value.toMutableMap()
        updatedMarkers[parcelleId] = position
        _permanentMarkers.value = updatedMarkers
        
        // Réinitialiser le marqueur temporaire
        _markerPosition.value = null
        
        Log.d("ParcellesViewModel", "Mode point terminé, marqueur permanent ajouté pour la parcelle $parcelleId")
    }

    private fun loadParcelles() {
        viewModelScope.launch {
            Log.d("ParcellesViewModel", "Début du chargement des parcelles")
            try {
                parcelleRepository.getAllParcelles().collect { parcellesList ->
                    Log.d("ParcellesViewModel", "Parcelles reçues: ${parcellesList.size}")
                    _parcelles.value = parcellesList
                    
                    // Mettre à jour les marqueurs permanents
                    val markerMap = mutableMapOf<String, GeoPoint>()
                    parcellesList.forEach { parcelle ->
                        markerMap[parcelle.id] = GeoPoint(parcelle.latitude, parcelle.longitude)
                    }
                    _permanentMarkers.value = markerMap
                }
            } catch (e: Exception) {
                Log.e("ParcellesViewModel", "Erreur lors du chargement des parcelles", e)
            }
        }
    }

    private fun observeParcelleToDelete() {
        viewModelScope.launch {
            _parcelleToDelete.collect { parcelle ->
                parcelle?.let {
                    deleteParcelle(it)
                    _parcelleToDelete.value = null
                }
            }
        }
    }

    fun selectParcelle(parcelle: Parcelle) {
        _selectedParcelle.value = parcelle
    }

    fun clearSelectedParcelle() {
        _selectedParcelle.value = null
    }

    fun setParcelleToDelete(parcelle: Parcelle) {
        _parcelleToDelete.value = parcelle
    }

    private fun deleteParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                // Mise à jour optimiste des états locaux
                // Supprimer la parcelle de la liste locale
                val updatedParcelles = _parcelles.value.toMutableList()
                updatedParcelles.removeIf { it.id == parcelle.id }
                _parcelles.value = updatedParcelles
                
                // Supprimer le marqueur permanent associé
                val updatedMarkers = _permanentMarkers.value.toMutableMap()
                updatedMarkers.remove(parcelle.id)
                _permanentMarkers.value = updatedMarkers
                
                // Opération asynchrone dans le repository
                parcelleRepository.deleteParcelle(parcelle)
                
                Log.d("ParcellesViewModel", "Parcelle supprimée avec succès")
            } catch (e: Exception) {
                Log.e("ParcellesViewModel", "Erreur lors de la suppression de la parcelle", e)
                // En cas d'erreur, le repository enverra une mise à jour avec les données correctes
            }
        }
    }

    fun addParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                // Mettre à jour immédiatement les états locaux avant l'opération asynchrone
                // Ajouter le marqueur aux marqueurs permanents
                val updatedMarkers = _permanentMarkers.value.toMutableMap()
                updatedMarkers[parcelle.id] = GeoPoint(parcelle.latitude, parcelle.longitude)
                _permanentMarkers.value = updatedMarkers
                
                // Ajouter temporairement la parcelle à la liste locale
                val updatedParcelles = _parcelles.value.toMutableList()
                updatedParcelles.add(parcelle)
                _parcelles.value = updatedParcelles
                
                // Si nous sommes en mode point et qu'un marqueur existe, l'associer à la parcelle
                if (_isPointMode.value && _markerPosition.value != null) {
                    finishPointMode(parcelle.id, _markerPosition.value!!)
                }
                
                // Enregistrement asynchrone dans le repository
                parcelleRepository.addParcelle(parcelle)
                
                Log.d("ParcellesViewModel", "Parcelle ajoutée avec succès")
            } catch (e: Exception) {
                Log.e("ParcellesViewModel", "Erreur lors de l'ajout de la parcelle", e)
                
                // En cas d'erreur, annuler les mises à jour locales
                // (le repository enverra une mise à jour avec les données correctes)
            }
        }
    }

    fun updateParcelle(parcelle: Parcelle) {
        viewModelScope.launch {
            try {
                // Mise à jour optimiste des états locaux
                // Mettre à jour la liste des parcelles
                val currentParcelles = _parcelles.value.toMutableList()
                val index = currentParcelles.indexOfFirst { it.id == parcelle.id }
                if (index >= 0) {
                    currentParcelles[index] = parcelle
                    _parcelles.value = currentParcelles
                }
                
                // Mettre à jour le marqueur permanent
                val updatedMarkers = _permanentMarkers.value.toMutableMap()
                updatedMarkers[parcelle.id] = GeoPoint(parcelle.latitude, parcelle.longitude)
                _permanentMarkers.value = updatedMarkers
                
                // Opération asynchrone dans le repository
                parcelleRepository.updateParcelle(parcelle)
                
                Log.d("ParcellesViewModel", "Parcelle mise à jour avec succès")
            } catch (e: Exception) {
                Log.e("ParcellesViewModel", "Erreur lors de la mise à jour de la parcelle", e)
                // En cas d'erreur, le repository enverra une mise à jour avec les données correctes
            }
        }
    }

    suspend fun getParcelleById(id: String): Parcelle? {
        return try {
            parcelleRepository.getParcelleById(id)
        } catch (e: Exception) {
            Log.e("ParcellesViewModel", "Erreur lors de la récupération de la parcelle", e)
            null
        }
    }

    private fun centerMapOnBeaune() {
        _mapCenter.value = GeoPoint(BEAUNE_LATITUDE, BEAUNE_LONGITUDE)
        _mapZoom.value = DEFAULT_ZOOM
        Log.d("ParcellesViewModel", "Carte centrée sur Beaune")
    }
} 