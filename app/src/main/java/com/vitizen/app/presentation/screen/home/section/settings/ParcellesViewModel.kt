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

    // Nouveaux états pour le mode polygon
    private val _polygonPoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val polygonPoints: StateFlow<List<GeoPoint>> = _polygonPoints.asStateFlow()

    private val _isDrawingPolygon = MutableStateFlow(false)
    val isDrawingPolygon: StateFlow<Boolean> = _isDrawingPolygon.asStateFlow()

    init {
        Log.d("ParcellesViewModel", "Initialisation du ViewModel")
        loadParcelles()
        observeParcelleToDelete()
        centerMapOnBeaune()
    }

    // Gestion des modes d'édition
    fun togglePointMode() {
        // Si on active le mode point, on désactive d'abord le mode polygon
        if (!_isPointMode.value) {
            _isPolygonMode.value = false
            _polygonPoints.value = emptyList()
            _isDrawingPolygon.value = false
        }
        
        // Basculer le mode point
        _isPointMode.value = !_isPointMode.value
        
        // Réinitialiser les marqueurs lors du changement de mode
        clearMarkers()
        
        Log.d("ParcellesViewModel", "Mode point unique: ${_isPointMode.value}, Mode polygon: ${_isPolygonMode.value}")
    }
    
    fun togglePolygonMode() {
        // Si on active le mode polygon, on désactive d'abord le mode point
        if (!_isPolygonMode.value) {
            _isPointMode.value = false
            clearMarkers()
        }
        
        // Basculer le mode polygon
        _isPolygonMode.value = !_isPolygonMode.value
        
        // Réinitialiser le polygon si on désactive le mode
        if (!_isPolygonMode.value) {
            _polygonPoints.value = emptyList()
            _isDrawingPolygon.value = false
        }
        
        Log.d("ParcellesViewModel", "Mode polygon: ${_isPolygonMode.value}, Mode point unique: ${_isPointMode.value}")
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

    // Fonctions pour le mode polygon
    fun addPolygonPoint(point: GeoPoint) {
        if (_isPolygonMode.value) {
            val currentPoints = _polygonPoints.value.toMutableList()
            
            // Vérifier si le polygon est fermé
            val isClosed = currentPoints.size > 2 && currentPoints.first() == currentPoints.last()
            
            if (isClosed) {
                // Trouver le segment le plus proche
                var minDistance = Double.MAX_VALUE
                var insertIndex = -1
                
                // Parcourir tous les segments (en excluant le dernier point qui est identique au premier)
                for (i in 0 until currentPoints.size - 1) {
                    val segmentStart = currentPoints[i]
                    val segmentEnd = currentPoints[i + 1]
                    
                    // Calculer la distance du point au segment
                    val distance = distanceToSegment(point, segmentStart, segmentEnd)
                    
                    if (distance < minDistance) {
                        minDistance = distance
                        insertIndex = i + 1
                    }
                }
                
                if (insertIndex != -1) {
                    // Insérer le point dans le segment le plus proche
                    currentPoints.add(insertIndex, point)
                    // Mettre à jour le dernier point pour maintenir la fermeture
                    currentPoints[currentPoints.size - 1] = currentPoints[0]
                    _polygonPoints.value = currentPoints
                    Log.d("ParcellesViewModel", "Point inséré dans le segment à l'index $insertIndex")
                }
            } else {
                // Comportement normal pour un polygon non fermé
                currentPoints.add(point)
                _polygonPoints.value = currentPoints
                Log.d("ParcellesViewModel", "Point ajouté au polygon: ${point.latitude}, ${point.longitude}, " +
                    "nombre total de points: ${currentPoints.size}")
            }
        } else {
            Log.d("ParcellesViewModel", "Mode polygon non actif, impossible d'ajouter un point")
        }
    }

    // Fonction utilitaire pour calculer la distance d'un point à un segment
    private fun distanceToSegment(point: GeoPoint, segmentStart: GeoPoint, segmentEnd: GeoPoint): Double {
        val x = point.latitude
        val y = point.longitude
        val x1 = segmentStart.latitude
        val y1 = segmentStart.longitude
        val x2 = segmentEnd.latitude
        val y2 = segmentEnd.longitude

        val A = x - x1
        val B = y - y1
        val C = x2 - x1
        val D = y2 - y1

        val dot = A * C + B * D
        val lenSq = C * C + D * D
        var param = -1.0

        if (lenSq != 0.0) {
            param = dot / lenSq
        }

        var xx: Double
        var yy: Double

        if (param < 0) {
            xx = x1
            yy = y1
        } else if (param > 1) {
            xx = x2
            yy = y2
        } else {
            xx = x1 + param * C
            yy = y1 + param * D
        }

        val dx = x - xx
        val dy = y - yy

        return Math.sqrt(dx * dx + dy * dy)
    }

    fun closePolygon() {
        if (_isPolygonMode.value && _polygonPoints.value.size > 2) {
            val currentPoints = _polygonPoints.value.toMutableList()
            // Vérifier si le polygon n'est pas déjà fermé
            if (currentPoints.first() != currentPoints.last()) {
                // Ajouter le premier point à la fin pour fermer le polygon
                currentPoints.add(currentPoints.first())
                _polygonPoints.value = currentPoints
                Log.d("ParcellesViewModel", "Polygon fermé avec ${currentPoints.size} points")
            } else {
                Log.d("ParcellesViewModel", "Polygon déjà fermé")
            }
        } else {
            Log.d("ParcellesViewModel", "Impossible de fermer le polygon - pas assez de points ou mode inactif")
        }
    }

    fun startDrawingPolygon() {
        if (_isPolygonMode.value) {
            _isDrawingPolygon.value = true
            _polygonPoints.value = emptyList()
            Log.d("ParcellesViewModel", "Début du dessin du polygon")
        }
    }

    fun finishDrawingPolygon() {
        if (_isPolygonMode.value && _isDrawingPolygon.value) {
            _isDrawingPolygon.value = false
            Log.d("ParcellesViewModel", "Fin du dessin du polygon avec ${_polygonPoints.value.size} points")
        }
    }

    fun clearPolygon() {
        _polygonPoints.value = emptyList()
        _isDrawingPolygon.value = false
        Log.d("ParcellesViewModel", "Polygon effacé")
    }

    /**
     * Supprime un point du polygon selon le mode actuel
     * @param pointIndex Index du point à supprimer (optionnel, si non fourni supprime le dernier point)
     */
    fun deletePolygonPoint(pointIndex: Int? = null) {
        val currentPoints = _polygonPoints.value.toMutableList()
        if (currentPoints.isEmpty()) {
            Log.d("ParcellesViewModel", "deletePolygonPoint - Liste de points vide")
            return
        }

        Log.d("ParcellesViewModel", "deletePolygonPoint - Début - Index à supprimer: $pointIndex")
        Log.d("ParcellesViewModel", "deletePolygonPoint - Points avant suppression: ${currentPoints.map { "${it.latitude},${it.longitude}" }}")

        val isClosed = currentPoints.size > 2 && currentPoints.first() == currentPoints.last()
        Log.d("ParcellesViewModel", "deletePolygonPoint - État du polygon:")
        Log.d("ParcellesViewModel", "- Nombre de points: ${currentPoints.size}")
        Log.d("ParcellesViewModel", "- Premier point: ${currentPoints.first().latitude},${currentPoints.first().longitude}")
        Log.d("ParcellesViewModel", "- Dernier point: ${currentPoints.last().latitude},${currentPoints.last().longitude}")
        Log.d("ParcellesViewModel", "- isClosed: $isClosed")

        if (isClosed) {
            Log.d("ParcellesViewModel", "deletePolygonPoint - Mode fermé détecté")
            if (currentPoints.size == 4) { // 3 points + point de fermeture
                Log.d("ParcellesViewModel", "deletePolygonPoint - Polygon fermé de 3 points")
                if (pointIndex != null) {
                    when (pointIndex) {
                        0, currentPoints.size - 1 -> {
                            // Si on supprime le premier point ou le point de fermeture
                            Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du premier/dernier point dans un polygon de 3 points")
                            val newPoints = mutableListOf<GeoPoint>()
                            newPoints.add(currentPoints[1])
                            newPoints.add(currentPoints[2])
                            _polygonPoints.value = newPoints
                            Log.d("ParcellesViewModel", "deletePolygonPoint - Nouveaux points après suppression: ${newPoints.map { "${it.latitude},${it.longitude}" }}")
                        }
                        else -> {
                            // Pour les autres points
                            Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du point $pointIndex dans un polygon de 3 points")
                            val newPoints = mutableListOf<GeoPoint>()
                            newPoints.add(currentPoints[0])
                            newPoints.add(currentPoints[if (pointIndex == 1) 2 else 1])
                            _polygonPoints.value = newPoints
                            Log.d("ParcellesViewModel", "deletePolygonPoint - Nouveaux points après suppression: ${newPoints.map { "${it.latitude},${it.longitude}" }}")
                        }
                    }
                }
            } else if (currentPoints.size > 4) {
                Log.d("ParcellesViewModel", "deletePolygonPoint - Polygon fermé avec plus de 3 points")
                if (pointIndex != null) {
                    if (pointIndex == 0 || pointIndex == currentPoints.size - 1) {
                        // Si on supprime le premier point ou le point de fermeture
                        Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du premier/dernier point dans un polygon fermé")
                        currentPoints.removeAt(0)
                        currentPoints.removeAt(currentPoints.size - 1)
                        currentPoints.add(currentPoints[0])
                        _polygonPoints.value = currentPoints
                        Log.d("ParcellesViewModel", "deletePolygonPoint - Nouveaux points après suppression: ${currentPoints.map { "${it.latitude},${it.longitude}" }}")
                    } else {
                        // Pour les autres points
                        Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du point $pointIndex dans un polygon fermé")
                        currentPoints.removeAt(pointIndex)
                        currentPoints[currentPoints.size - 1] = currentPoints[0]
                        _polygonPoints.value = currentPoints
                        Log.d("ParcellesViewModel", "deletePolygonPoint - Nouveaux points après suppression: ${currentPoints.map { "${it.latitude},${it.longitude}" }}")
                    }
                }
            }
        } else {
            Log.d("ParcellesViewModel", "deletePolygonPoint - Mode ouvert détecté")
            if (pointIndex != null && pointIndex < currentPoints.size) {
                Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du point $pointIndex en mode ouvert")
                currentPoints.removeAt(pointIndex)
            } else {
                Log.d("ParcellesViewModel", "deletePolygonPoint - Suppression du dernier point en mode ouvert")
                currentPoints.removeAt(currentPoints.size - 1)
            }
            _polygonPoints.value = currentPoints
            Log.d("ParcellesViewModel", "deletePolygonPoint - Nouveaux points après suppression: ${currentPoints.map { "${it.latitude},${it.longitude}" }}")
        }
        Log.d("ParcellesViewModel", "deletePolygonPoint - Fin")
    }

    /**
     * Gère le clic sur un point du polygon
     * @param pointIndex Index du point cliqué
     */
    fun handlePolygonPointClick(pointIndex: Int) {
        val currentPoints = _polygonPoints.value
        if (currentPoints.isEmpty()) {
            Log.d("ParcellesViewModel", "handlePolygonPointClick - Liste de points vide")
            return
        }

        Log.d("ParcellesViewModel", "handlePolygonPointClick - Début - Point cliqué: $pointIndex")
        Log.d("ParcellesViewModel", "handlePolygonPointClick - Points actuels: ${currentPoints.map { "${it.latitude},${it.longitude}" }}")
        
        // Vérifier si le polygon est fermé en comparant le premier et le dernier point
        val isClosed = currentPoints.size > 2 && currentPoints.first() == currentPoints.last()
        val isFirstPoint = pointIndex == 0
        val isLastPoint = pointIndex == currentPoints.size - 1
        
        Log.d("ParcellesViewModel", "handlePolygonPointClick - État du polygon:")
        Log.d("ParcellesViewModel", "- Nombre de points: ${currentPoints.size}")
        Log.d("ParcellesViewModel", "- Premier point: ${currentPoints.first().latitude},${currentPoints.first().longitude}")
        Log.d("ParcellesViewModel", "- Dernier point: ${currentPoints.last().latitude},${currentPoints.last().longitude}")
        Log.d("ParcellesViewModel", "- isClosed: $isClosed")
        Log.d("ParcellesViewModel", "- isFirstPoint: $isFirstPoint")
        Log.d("ParcellesViewModel", "- isLastPoint: $isLastPoint")

        when {
            // Si le polygon est fermé, on supprime toujours le point cliqué
            isClosed -> {
                Log.d("ParcellesViewModel", "handlePolygonPointClick - Mode fermé détecté, suppression du point $pointIndex")
                deletePolygonPoint(pointIndex)
            }
            // Si on clique sur le premier point en mode ouvert avec plus de 2 points
            isFirstPoint && currentPoints.size > 2 -> {
                Log.d("ParcellesViewModel", "handlePolygonPointClick - Tentative de fermeture du polygon")
                closePolygon()
            }
            // Pour tous les autres cas en mode ouvert
            else -> {
                Log.d("ParcellesViewModel", "handlePolygonPointClick - Mode ouvert, suppression du point $pointIndex")
                deletePolygonPoint(pointIndex)
            }
        }
        Log.d("ParcellesViewModel", "handlePolygonPointClick - Fin")
    }
} 