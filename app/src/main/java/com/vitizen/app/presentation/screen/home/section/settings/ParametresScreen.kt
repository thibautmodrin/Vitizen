@file:Suppress("DEPRECATION")

package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.domain.model.InformationsGenerales
import com.vitizen.app.domain.model.Operateur
import com.vitizen.app.domain.model.Parcelle
import com.vitizen.app.domain.model.Pulverisateur
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.views.CustomZoomButtonsController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import org.osmdroid.views.overlay.Polygon
import android.graphics.Color as AndroidColor
import kotlin.math.abs
import java.util.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.input.pointer.pointerInput
import com.vitizen.app.R
import org.osmdroid.api.IGeoPoint
import kotlin.math.sqrt
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.BorderStroke as FoundationBorderStroke

data class TabItem(
    val title: String,
    val icon: ImageVector
)

data class ParcelleInfo(
    val name: String,
    val surface: String,
    val cepage: String,
    val latitude: Double,
    val longitude: Double
)

data class PolygonPoint(
    val point: GeoPoint,
    val isParcelle: Boolean = false
)

// Constantes de base pour la précision de détection
private const val BASE_POINT_DETECTION_THRESHOLD = 30  // Distance en pixels pour détecter un point existant
private const val BASE_SEGMENT_DETECTION_THRESHOLD = 0.0001  // Distance en degrés pour la détection de segments
private const val BASE_POLYGON_CLOSURE_THRESHOLD = 100.0  // Distance en pixels pour fermer le polygone

// Fonction pour calculer les seuils en fonction du zoom
private fun calculateThresholds(mapView: MapView): Triple<Double, Double, Double> {
    val zoomLevel = mapView.zoomLevelDouble
    val zoomFactor = when {
        zoomLevel >= 18.0 -> 0.5  // Zoom très proche
        zoomLevel >= 16.0 -> 0.7  // Zoom proche
        zoomLevel >= 14.0 -> 1.0  // Zoom normal
        zoomLevel >= 12.0 -> 1.5  // Zoom éloigné
        else -> 2.0  // Zoom très éloigné
    }
    
    return Triple(
        BASE_POINT_DETECTION_THRESHOLD * zoomFactor,
        BASE_SEGMENT_DETECTION_THRESHOLD * zoomFactor,
        BASE_POLYGON_CLOSURE_THRESHOLD * zoomFactor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToForm: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = remember {
        listOf(
            TabItem("Infos", Icons.Default.Info),
            TabItem("Opérateurs", Icons.Default.Person),
            TabItem("Pulvérisateurs", Icons.Default.Build),
            TabItem("Parcelles", Icons.Default.Landscape)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { 
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    },
                    icon = { Icon(tab.icon, contentDescription = tab.title) }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTabIndex) {
                0 -> InformationsBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                1 -> OperateursBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                2 -> PulverisateursBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                3 -> ParcellesBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
            }
        }
    }
}

@Composable
fun InformationsBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val informationsGenerales by viewModel.informationsGenerales.collectAsState()
    var informationToDelete by remember { mutableStateOf<InformationsGenerales?>(null) }

    LaunchedEffect(informationToDelete) {
        informationToDelete?.let { info ->
            viewModel.deleteInformationsGenerales(info)
            informationToDelete = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (informationsGenerales.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = informationsGenerales,
                        key = { it.id }
                    ) { info ->
                        InformationItem(
                            information = info,
                            onEdit = { onNavigateToForm("generalInfo/${info.id}") },
                            onDelete = { informationToDelete = info }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune information générale enregistrée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onNavigateToForm("generalInfo") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter des informations"
            )
        }
    }
}

@Composable
fun OperateursBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val operateurs by viewModel.operateurs.collectAsState()
    var operateurToDelete by remember { mutableStateOf<Operateur?>(null) }

    LaunchedEffect(operateurToDelete) {
        operateurToDelete?.let { operateur ->
            viewModel.deleteOperateur(operateur)
            operateurToDelete = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (operateurs.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = operateurs,
                        key = { it.id }
                    ) { operateur ->
                        OperateurItem(
                            operateur = operateur,
                            onEdit = { onNavigateToForm("operateur/${operateur.id}") },
                            onDelete = { operateurToDelete = operateur }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun opérateur enregistré",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onNavigateToForm("operateur") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter un opérateur"
            )
        }
    }
}

@Composable
fun PulverisateursBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val pulverisateurs by viewModel.pulverisateurs.collectAsState()
    var pulverisateurToDelete by remember { mutableStateOf<Pulverisateur?>(null) }

    LaunchedEffect(pulverisateurToDelete) {
        pulverisateurToDelete?.let { pulverisateur ->
            viewModel.deletePulverisateur(pulverisateur)
            pulverisateurToDelete = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (pulverisateurs.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = pulverisateurs,
                        key = { it.id }
                    ) { pulverisateur ->
                        PulverisateurItem(
                            pulverisateur = pulverisateur,
                            onEdit = { onNavigateToForm("pulverisateur/${pulverisateur.id}") },
                            onDelete = { pulverisateurToDelete = pulverisateur }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun pulvérisateur enregistré",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onNavigateToForm("pulverisateur") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter un pulvérisateur"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcellesBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isMapReady by remember { mutableStateOf(false) }
    var selectedMarker: Marker? by remember { mutableStateOf(null) }
    var isSatelliteView by remember { mutableStateOf(false) }
    var selectedLatitude by remember { mutableDoubleStateOf(46.603354) }
    var selectedLongitude by remember { mutableDoubleStateOf(1.888334) }
    var showParcelleDialog by remember { mutableStateOf(false) }
    var newParcelle by remember { mutableStateOf(ParcelleInfo("", "", "", 0.0, 0.0)) }
    var isEditingParcelle by remember { mutableStateOf(false) }
    var parcelleToEdit by remember { mutableStateOf<Parcelle?>(null) }
    var myLocationOverlay: MyLocationNewOverlay? by remember { mutableStateOf(null) }
    var parcelleMarkers by remember { mutableStateOf(listOf<Marker>()) }
    val parcelles by viewModel.parcelles.collectAsState()

    // Modifiez les états pour le mode polygone
    var modePolygoneActif by remember { mutableStateOf(false) }
    var polygonPoints by remember { mutableStateOf(mutableListOf<PolygonPoint>()) }
    var drawnPolyline: PolylineOverlay? by remember { mutableStateOf(null) }
    var polygonMarkers by remember { mutableStateOf(mutableListOf<Marker>()) }
    var polygonPointsCount by remember { mutableIntStateOf(0) }
    var showHelpText by remember { mutableStateOf(false) }
    var isPolygonClosed by remember { mutableStateOf(false) }
    var drawnPolygon: Polygon? by remember { mutableStateOf(null) }

    // Ajouter l'état pour stocker les polygones des parcelles
    var parcellePolygons by remember { mutableStateOf(mapOf<String, Polygon>()) }

    var selectedParcelleId by remember { mutableStateOf<String?>(null) }

    // Fonctions de gestion des polygones
    fun updatePolygon(mapView: MapView) {
        if (isPolygonClosed) {
            drawnPolygon?.let { mapView.overlays.remove(it) }
            drawnPolygon = Polygon().apply {
                points = polygonPoints.map { it.point }
                fillColor = AndroidColor.argb(60, 0, 0, 255)
                strokeColor = AndroidColor.BLUE
                strokeWidth = 4f
            }
            mapView.overlays.add(drawnPolygon)
        } else {
            drawnPolyline?.let { mapView.overlays.remove(it) }
            if (polygonPoints.size >= 2) {
                drawnPolyline = PolylineOverlay(polygonPoints.map { it.point }).apply {
                    strokeColor = AndroidColor.BLUE
                    strokeWidth = 4f
                }
                mapView.overlays.add(drawnPolyline)
            }
        }
    }

    fun updateMarkerTitles() {
        polygonMarkers.forEachIndexed { index, marker ->
            marker.title = "Point ${index + 1}"
        }
    }

    fun handleClosedPolygonTouch(geoPoint: IGeoPoint, mapView: MapView): Boolean {
        Log.d("MapEvents", "=== GESTION POLYGONE FERMÉ ===")
        val (pointThreshold, segmentThreshold, _) = calculateThresholds(mapView)
        Log.d("MapEvents", "Seuils actuels - Point: $pointThreshold, Segment: $segmentThreshold")
        
        var minDistance = Double.MAX_VALUE
        var closestPointIndex = -1
        
        polygonPoints.forEachIndexed { index, polygonPoint ->
            val distance = polygonPoint.point.distanceToAsDouble(geoPoint)
            Log.d("MapEvents", "Distance au point $index: $distance")
            if (distance < minDistance) {
                minDistance = distance
                closestPointIndex = index
            }
        }
        
        Log.d("MapEvents", "Point le plus proche: index=$closestPointIndex, distance=$minDistance")
        
        if (minDistance < pointThreshold) {
            Log.d("MapEvents", "Clic sur un point existant - Suppression")
            Log.d("MapEvents", "Nombre de points avant suppression: ${polygonPoints.size}")
            
            // Supprimer le point
            polygonPoints.removeAt(closestPointIndex)
            polygonMarkers[closestPointIndex].let { marker ->
                mapView.overlays.remove(marker)
                polygonMarkers.removeAt(closestPointIndex)
            }
            
            Log.d("MapEvents", "Nombre de points après suppression: ${polygonPoints.size}")
            
            // Vérifier si on doit passer en mode polygone ouvert
            if (polygonPoints.size < 3) {
                Log.d("MapEvents", "Passage en mode polygone ouvert - moins de 3 points")
                isPolygonClosed = false
                drawnPolygon?.let { mapView.overlays.remove(it) }
                drawnPolygon = null
            }
            
            // Mettre à jour le polygone
            updatePolygon(mapView)
            
            // Mettre à jour les titres des marqueurs
            updateMarkerTitles()
            
            mapView.invalidate()
            Log.d("MapEvents", "Point supprimé et polygone mis à jour")
            return true
        } else {
            // Vérifier si le point est à l'intérieur du polygone
            if (isPointInPolygon(geoPoint, polygonPoints.map { it.point })) {
                Log.d("MapEvents", "Point à l'intérieur du polygone - insertion")
                val newPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                
                // Trouver le segment le plus proche
                var minSegmentDistance = Double.MAX_VALUE
                var insertIndex = -1
                
                for (i in 0 until polygonPoints.size) {
                    val currentPoint = polygonPoints[i].point
                    val nextPoint = polygonPoints[(i + 1) % polygonPoints.size].point
                    
                    val distanceToSegment = distancePointToSegment(newPoint, currentPoint, nextPoint)
                    Log.d("MapEvents", "Distance au segment $i: $distanceToSegment")
                    if (distanceToSegment < minSegmentDistance) {
                        minSegmentDistance = distanceToSegment
                        insertIndex = i + 1
                    }
                }
                
                Log.d("MapEvents", "Distance minimale au segment: $minSegmentDistance")
                Log.d("MapEvents", "Index d'insertion choisi: $insertIndex")
                
                if (minSegmentDistance < segmentThreshold) {
                    Log.d("MapEvents", "Point trop proche d'un segment existant")
                    return true
                }
                
                // Insérer le nouveau point
                polygonPoints.add(insertIndex, PolygonPoint(newPoint, false))
                
                val newMarker = Marker(mapView).apply {
                    position = newPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_polygon)
                    infoWindow = null
                    title = "Point ${insertIndex + 1}"
                }
                polygonMarkers.add(insertIndex, newMarker)
                mapView.overlays.add(newMarker)
                
                // Mettre à jour le polygone
                drawnPolygon?.let { mapView.overlays.remove(it) }
                drawnPolygon = Polygon().apply {
                    points = polygonPoints.map { it.point }
                    fillColor = AndroidColor.argb(60, 0, 0, 255)
                    strokeColor = AndroidColor.BLUE
                    strokeWidth = 4f
                }
                mapView.overlays.add(drawnPolygon)
                mapView.invalidate()
                
                Log.d("MapEvents", "Nouveau point inséré et polygone mis à jour")
                return true
            }
        }
        
        Log.d("MapEvents", "=== FIN GESTION POLYGONE FERMÉ ===")
        return false
    }

    fun handleOpenPolygonTouch(geoPoint: IGeoPoint, mapView: MapView): Boolean {
        Log.d("MapEvents", "=== GESTION POLYGONE OUVERT ===")
        val (pointThreshold, _, _) = calculateThresholds(mapView)
        Log.d("MapEvents", "Seuil de détection des points: $pointThreshold")
        
        var minDistance = Double.MAX_VALUE
        var closestPointIndex = -1
        
        polygonPoints.forEachIndexed { index, polygonPoint ->
            val distance = polygonPoint.point.distanceToAsDouble(geoPoint)
            Log.d("MapEvents", "Distance au point $index: $distance")
            if (distance < minDistance) {
                minDistance = distance
                closestPointIndex = index
            }
        }
        
        Log.d("MapEvents", "Point le plus proche: index=$closestPointIndex, distance=$minDistance")
        
        if (minDistance < pointThreshold) {
            // Vérifier si on peut fermer le polygone
            if (closestPointIndex == 0 && polygonPoints.size >= 3) {
                Log.d("MapEvents", "Fermeture du polygone")
                isPolygonClosed = true
                drawnPolyline?.let { mapView.overlays.remove(it) }
                drawnPolyline = null
                
                drawnPolygon = Polygon().apply {
                    points = polygonPoints.map { it.point }
                    fillColor = AndroidColor.argb(60, 0, 0, 255)
                    strokeColor = AndroidColor.BLUE
                    strokeWidth = 4f
                }
                mapView.overlays.add(drawnPolygon)
                mapView.invalidate()
                return true
            }
            
            // Supprimer le point
            Log.d("MapEvents", "Suppression du point $closestPointIndex")
            polygonPoints.removeAt(closestPointIndex)
            polygonMarkers[closestPointIndex].let { marker ->
                mapView.overlays.remove(marker)
                polygonMarkers.removeAt(closestPointIndex)
            }
            
            // Vérifier si on doit passer en mode classique
            if (polygonPoints.size < 3) {
                Log.d("MapEvents", "Passage en mode classique - moins de 3 points")
                isPolygonClosed = false
                drawnPolygon?.let { mapView.overlays.remove(it) }
                drawnPolygon = null
            }
            
            // Mettre à jour le polygone
            updatePolygon(mapView)
            
            // Mettre à jour les titres des marqueurs
            updateMarkerTitles()
            
            mapView.invalidate()
            Log.d("MapEvents", "Point supprimé et polygone mis à jour")
            return true
        }
        
        Log.d("MapEvents", "=== FIN GESTION POLYGONE OUVERT ===")
        return false
    }

    // Fonction de suppression d'une parcelle
    fun deleteParcelle(parcelle: Parcelle) {
        Log.d("MapEvents", "Suppression de la parcelle: ${parcelle.name}")
        
        // Supprimer le polygone de la map des polygones
        parcellePolygons[parcelle.id]?.let { polygon ->
            Log.d("MapEvents", "Suppression du polygone de la parcelle de la map")
            mapView.overlays.remove(polygon)
            parcellePolygons = parcellePolygons - parcelle.id
        }
        
        // Supprimer uniquement le marqueur associé à cette parcelle
        parcelleMarkers.find { marker ->
            abs(marker.position.latitude - parcelle.latitude) < 0.0001 &&
            abs(marker.position.longitude - parcelle.longitude) < 0.0001
        }?.let { marker ->
            Log.d("MapEvents", "Suppression du marqueur de la parcelle")
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
            }
            mapView.overlays.remove(marker)
            parcelleMarkers = parcelleMarkers.filter { it != marker }
        }
        
        // Supprimer la parcelle de la base de données
        viewModel.deleteParcelle(parcelle)
        
        // Forcer la mise à jour de la carte
        mapView.invalidate()
        Log.d("MapEvents", "Suppression de la parcelle terminée")
    }

    LaunchedEffect(modePolygoneActif, polygonPoints) {
        polygonPointsCount = polygonPoints.size
        showHelpText = modePolygoneActif && polygonPoints.size < 3
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Effet pour la gestion de la vue satellite
    LaunchedEffect(isSatelliteView, isMapReady) {
        if (isMapReady) {
            try {
                mapView.setTileSource(if (isSatelliteView) TileSourceFactory.ChartbundleENRH else TileSourceFactory.MAPNIK)
                mapView.invalidate()
            } catch (e: Exception) {
                Log.e("ParcellesBox", "Erreur lors du changement de vue", e)
            }
        }
    }

    // Effet pour la gestion des parcelles
    LaunchedEffect(parcelles, isMapReady) {
        if (isMapReady) {
            try {
                // Mise à jour des marqueurs et polygones
            parcelleMarkers.forEach { marker ->
                mapView.overlays.remove(marker)
            }
            parcelleMarkers = emptyList()

                parcellePolygons.values.forEach { polygon ->
                    mapView.overlays.remove(polygon)
                }
                parcellePolygons = emptyMap()

                parcelles.forEach { parcelle ->
                    if (parcelle.polygonPoints.isEmpty()) {
                        val marker = Marker(mapView).apply {
                    position = GeoPoint(parcelle.latitude, parcelle.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_gray)
                    title = parcelle.name
                    snippet = "${parcelle.surface} ha • ${parcelle.cepage}"
                }
                        parcelleMarkers = parcelleMarkers + marker
                mapView.overlays.add(marker)
                    } else {
                        val polygon = Polygon().apply {
                            points = parcelle.polygonPoints
                            fillColor = AndroidColor.argb(60, 0, 0, 255)
                            strokeColor = AndroidColor.BLUE
                            strokeWidth = 4f
                        }
                        mapView.overlays.add(polygon)
                        parcellePolygons = parcellePolygons + (parcelle.id to polygon)
                    }
            }
            mapView.invalidate()
            } catch (e: Exception) {
                Log.e("ParcellesBox", "Erreur lors de la mise à jour des parcelles", e)
            }
        }
    }

    // Fonction pour réinitialiser le formulaire
    fun resetForm() {
        newParcelle = ParcelleInfo("", "", "", 0.0, 0.0)
        selectedLatitude = 46.603354
        selectedLongitude = 1.888334
        modePolygoneActif = false
        polygonPoints.clear()
        polygonMarkers.clear()
        drawnPolyline?.let { mapView.overlays.remove(it) }
        drawnPolyline = null
        mapView.invalidate()
        isEditingParcelle = false
        parcelleToEdit = null
    }

    // Dialog pour ajouter/modifier une parcelle
    if (showParcelleDialog) {
        AlertDialog(
            onDismissRequest = { 
                showParcelleDialog = false
                isEditingParcelle = false
                parcelleToEdit = null
            },
            title = { Text(if (isEditingParcelle) "Modifier la parcelle" else "Nouvelle parcelle") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newParcelle.name,
                        onValueChange = { newParcelle = newParcelle.copy(name = it) },
                        label = { Text("Nom de la parcelle") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newParcelle.surface,
                        onValueChange = { newParcelle = newParcelle.copy(surface = it) },
                        label = { Text("Surface (ha)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newParcelle.cepage,
                        onValueChange = { newParcelle = newParcelle.copy(cepage = it) },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newParcelle.name.isNotBlank() && newParcelle.surface.isNotBlank() && newParcelle.cepage.isNotBlank()) {
                            if (isEditingParcelle && parcelleToEdit != null) {
                                val parcelle = parcelleToEdit!!
                                // Mise à jour d'une parcelle existante
                                val updatedParcelle = parcelle.copy(
                                    name = newParcelle.name,
                                    surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                    cepage = newParcelle.cepage,
                                    latitude = parcelle.latitude,
                                    longitude = parcelle.longitude,
                                    polygonPoints = parcelle.polygonPoints
                                )
                                viewModel.updateParcelle(updatedParcelle)
                            } else {
                                // Création d'une nouvelle parcelle
                                val parcelleToAdd = Parcelle(
                                    id = UUID.randomUUID().toString(),
                                    name = newParcelle.name,
                                    surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                    cepage = newParcelle.cepage,
                                    typeConduite = "",
                                    largeur = 0.0,
                                    hauteur = 0.0,
                                    latitude = if (modePolygoneActif) 0.0 else selectedLatitude,
                                    longitude = if (modePolygoneActif) 0.0 else selectedLongitude,
                                    polygonPoints = if (modePolygoneActif) polygonPoints.map { it.point } else emptyList()
                                )
                                viewModel.addParcelle(parcelleToAdd)
                            }
                            showParcelleDialog = false
                            isEditingParcelle = false
                            parcelleToEdit = null
                            
                            // Réinitialiser le formulaire et le mode polygone seulement après la validation
                            if (modePolygoneActif) {
                                // Supprimer le polygone temporaire
                                drawnPolygon?.let { mapView.overlays.remove(it) }
                                drawnPolygon = null
                                
                                // Supprimer uniquement les marqueurs des points de construction
                                polygonMarkers.forEach { marker ->
                                    mapView.overlays.remove(marker)
                                }
                                polygonMarkers.clear()
                                polygonPoints.clear()
                                
                                // Désactiver le mode polygone
                                modePolygoneActif = false
                                isPolygonClosed = false
                            } else {
                                // Changer l'icône du marqueur en gris après validation
                                selectedMarker?.let { _ ->
                                    //  marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_gray)
                                    mapView.invalidate()
                                }
                            }
                            mapView.invalidate()
                        }
                    }
                ) {
                    Text(if (isEditingParcelle) "Modifier" else "Ajouter")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showParcelleDialog = false
                        isEditingParcelle = false
                        parcelleToEdit = null
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Carte (70% de la hauteur)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (!isMapReady) {
                        try {
                            view.apply {
                                controller.setZoom(15.0)
                                controller.setCenter(GeoPoint(selectedLatitude, selectedLongitude))

                                // Configuration des contrôles de zoom
                                setMultiTouchControls(true)
                                setBuiltInZoomControls(false)
                                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                                // Gestion du double-clic pour centrer sur toutes les parcelles
                                overlays.add(object : Overlay() {
                                    override fun onDoubleTap(e: MotionEvent, mapView: MapView): Boolean {
                                        val parcelles = viewModel.parcelles.value
                                        if (parcelles.isNotEmpty()) {
                                            // Réinitialiser la sélection de la carte
                                            selectedParcelleId = null
                                            
                                            var minLat = Double.MAX_VALUE
                                            var maxLat = Double.MIN_VALUE
                                            var minLon = Double.MAX_VALUE
                                            var maxLon = Double.MIN_VALUE
                                            
                                            parcelles.forEach { parcelle ->
                                                if (parcelle.polygonPoints.isNotEmpty()) {
                                                    // Pour les parcelles avec polygone
                                                    parcelle.polygonPoints.forEach { point ->
                                                        minLat = minOf(minLat, point.latitude)
                                                        maxLat = maxOf(maxLat, point.latitude)
                                                        minLon = minOf(minLon, point.longitude)
                                                        maxLon = maxOf(maxLon, point.longitude)
                                                    }
                                                } else {
                                                    // Pour les parcelles avec un seul point
                                                    minLat = minOf(minLat, parcelle.latitude)
                                                    maxLat = maxOf(maxLat, parcelle.latitude)
                                                    minLon = minOf(minLon, parcelle.longitude)
                                                    maxLon = maxOf(maxLon, parcelle.longitude)
                                                }
                                            }
                                            
                                            // Ajouter une marge de 10%
                                            val latMargin = (maxLat - minLat) * 0.1
                                            val lonMargin = (maxLon - minLon) * 0.1
                                            
                                            val boundingBox = org.osmdroid.util.BoundingBox(
                                                maxLat + latMargin,
                                                maxLon + lonMargin,
                                                minLat - latMargin,
                                                minLon - lonMargin
                                            )
                                            
                                            // Animer le zoom vers la boîte englobante
                                            mapView.zoomToBoundingBox(boundingBox, true, 15)
                                        }
                                        return true
                                    }
                                })

                                // Fixer l'orientation vers le nord
                                setMapOrientation(0f)

                                // Ajout de l'overlay de localisation si la permission est accordée
                                if (hasLocationPermission) {
                                    myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this).apply {
                                        enableMyLocation()
                                        disableFollowLocation()
                                        disableMyLocation()
                                    }
                                    overlays.add(myLocationOverlay!!)
                                }

                                // Ajout de l'overlay de sélection en premier
                                overlays.add(object : Overlay() {
                                    override fun onTouchEvent(event: MotionEvent, mapView: MapView): Boolean {
                                        if (event.action == MotionEvent.ACTION_UP && modePolygoneActif) {
                                            val projection = mapView.projection
                                            val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                                            
                                            // Calculer les seuils
                                            val (pointThreshold, _, _) = calculateThresholds(mapView)
                                            
                                            // Vérifier si on clique sur un point existant
                                            var minDistance = Double.MAX_VALUE
                                            var closestPointIndex = -1
                                            
                                            polygonPoints.forEachIndexed { index, polygonPoint ->
                                                val distance = polygonPoint.point.distanceToAsDouble(geoPoint)
                                                if (distance < minDistance) {
                                                    minDistance = distance
                                                    closestPointIndex = index
                                                }
                                            }
                                            
                                            // Si on clique sur un point existant
                                            if (minDistance < pointThreshold) {
                                                // Vérifier si on peut fermer le polygone
                                                if (closestPointIndex == 0 && polygonPoints.size >= 3 && !isPolygonClosed) {
                                                    Log.d("MapEvents", "Fermeture du polygone")
                                                    isPolygonClosed = true
                                                    drawnPolyline?.let { mapView.overlays.remove(it) }
                                                    drawnPolyline = null
                                                    
                                                    drawnPolygon = Polygon().apply {
                                                        points = polygonPoints.map { it.point }
                                                        fillColor = AndroidColor.argb(60, 0, 0, 255)
                                                        strokeColor = AndroidColor.BLUE
                                                        strokeWidth = 4f
                                                    }
                                                    mapView.overlays.add(drawnPolygon)
                                                    mapView.invalidate()
                                                    return true
                                                }
                                                
                                                // Vérifier si on peut supprimer le premier point
                                                if (closestPointIndex == 0) {
                                                    if (polygonPoints.size <= 2) {
                                                        // Permettre la suppression du premier point s'il n'y a que 2 points ou moins
                                                        Log.d("MapEvents", "Suppression du premier point autorisée - moins de 3 points")
                                                    } else if (!isPolygonClosed) {
                                                        Log.d("MapEvents", "Impossible de supprimer le premier point - polygone non fermé et plus de 2 points")
                                                        return true
                                                    }
                                                }

                                                Log.d("MapEvents", "Suppression du point $closestPointIndex")
                                                polygonPoints.removeAt(closestPointIndex)
                                                polygonMarkers[closestPointIndex].let { marker ->
                                                    mapView.overlays.remove(marker)
                                                    polygonMarkers.removeAt(closestPointIndex)
                                                }
                                                
                                                // Vérifier si on doit passer en mode classique
                                                if (polygonPoints.size < 3) {
                                                    Log.d("MapEvents", "Passage en mode classique - moins de 3 points")
                                                    isPolygonClosed = false
                                                    drawnPolygon?.let { mapView.overlays.remove(it) }
                                                    drawnPolygon = null
                                                }
                                                
                                                // Mettre à jour le polygone si fermé
                                                if (isPolygonClosed) {
                                                    drawnPolygon?.let { mapView.overlays.remove(it) }
                                                    drawnPolygon = Polygon().apply {
                                                        points = polygonPoints.map { it.point }
                                                        fillColor = AndroidColor.argb(60, 0, 0, 255)
                                                        strokeColor = AndroidColor.BLUE
                                                        strokeWidth = 4f
                                                    }
                                                    mapView.overlays.add(drawnPolygon)
                                                } else {
                                                    drawnPolyline?.let { mapView.overlays.remove(it) }
                                                    if (polygonPoints.size >= 2) {
                                                        drawnPolyline = PolylineOverlay(polygonPoints.map { it.point }).apply {
                                                            strokeColor = AndroidColor.BLUE
                                                            strokeWidth = 4f
                                                        }
                                                        mapView.overlays.add(drawnPolyline)
                                                    }
                                                }
                                                
                                                // Mettre à jour les titres des marqueurs restants
                                                polygonMarkers.forEachIndexed { index, marker ->
                                                    marker.title = "Point ${index + 1}"
                                                }
                                                
                                                mapView.invalidate()
                                                Log.d("MapEvents", "Point supprimé. Nouveau nombre de points: ${polygonPoints.size}")
                                                return true
                                            }
                                        }
                                        return false
                                    }

                                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                                        Log.d("MapEvents", "=== DÉBUT ÉVÉNEMENT SINGLE TAP ===")
                                        Log.d("MapEvents", "Position du clic: x=${e.x}, y=${e.y}")
                                        
                                        val projection = mapView.projection
                                        val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                        Log.d("MapEvents", "Point géographique: lat=${geoPoint.latitude}, lon=${geoPoint.longitude}")
                                        
                                        if (modePolygoneActif) {
                                            // Calculer les seuils
                                            val (pointThreshold, segmentThreshold, _) = calculateThresholds(mapView)
                                            
                                            Log.d("MapEvents", "Mode polygone actif")
                                            Log.d("MapEvents", "État actuel:")
                                            Log.d("MapEvents", "- Nombre de points: ${polygonPoints.size}")
                                            Log.d("MapEvents", "- Polygone fermé: $isPolygonClosed")
                                            
                                            // Vérifier si on clique sur un point existant
                                            var minDistance = Double.MAX_VALUE
                                            var closestPointIndex = -1
                                            
                                            polygonPoints.forEachIndexed { index, polygonPoint ->
                                                val distance = polygonPoint.point.distanceToAsDouble(geoPoint)
                                                Log.d("MapEvents", "Distance au point $index: $distance")
                                                if (distance < minDistance) {
                                                    minDistance = distance
                                                    closestPointIndex = index
                                                }
                                            }
                                            
                                            Log.d("MapEvents", "Point le plus proche: index=$closestPointIndex, distance=$minDistance")
                                            
                                            if (minDistance < pointThreshold) {
                                                // Si on clique sur un point existant, on ne fait rien ici
                                                // La suppression est gérée dans onTouchEvent
                                                return true
                                            } else if (!isPolygonClosed || (isPolygonClosed && polygonPoints.size >= 3)) {
                                                // Ajout d'un nouveau point
                                                Log.d("MapEvents", "Ajout d'un nouveau point")
                                                val newPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                                
                                                if (polygonPoints.isEmpty()) {
                                                    // Premier point du polygone
                                                    Log.d("MapEvents", "Ajout du premier point")
                                                    polygonPoints.add(PolygonPoint(newPoint, false))
                                                    
                                                    val newMarker = Marker(mapView).apply {
                                                        position = newPoint
                                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                                        icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_polygon)
                                                        infoWindow = null
                                                        title = "Point 1"
                                                    }
                                                    polygonMarkers.add(newMarker)
                                                    mapView.overlays.add(newMarker)
                                                } else if (isPolygonClosed) {
                                                    // Mode insertion uniquement si le polygone est fermé
                                                    Log.d("MapEvents", "=== MODE INSERTION POLYGONE FERMÉ ===")
                                                    Log.d("MapEvents", "Nombre de points actuels: ${polygonPoints.size}")
                                                    var minSegmentDistance = Double.MAX_VALUE
                                                    var insertIndex = -1
                                                    
                                                    for (i in 0 until polygonPoints.size) {
                                                        val currentPoint = polygonPoints[i].point
                                                        val nextPoint = polygonPoints[(i + 1) % polygonPoints.size].point
                                                        
                                                        Log.d("MapEvents", "Analyse du segment $i")
                                                        val distanceToSegment = distancePointToSegment(newPoint, currentPoint, nextPoint)
                                                        Log.d("MapEvents", "Distance au segment $i: $distanceToSegment")
                                                        
                                                        if (distanceToSegment < minSegmentDistance) {
                                                            minSegmentDistance = distanceToSegment
                                                            insertIndex = i + 1
                                                            Log.d("MapEvents", "Nouveau segment le plus proche trouvé: $i (distance: $distanceToSegment)")
                                                        }
                                                    }
                                                    
                                                    Log.d("MapEvents", "Distance minimale trouvée: $minSegmentDistance")
                                                    Log.d("MapEvents", "Index d'insertion choisi: $insertIndex")
                                                    
                                                    if (minSegmentDistance < segmentThreshold) {
                                                        Log.d("MapEvents", "Point trop proche d'un segment existant, insertion annulée")
                                                        return true
                                                    }
                                                    
                                                    Log.d("MapEvents", "Insertion du point à l'index $insertIndex")
                                                    polygonPoints.add(insertIndex, PolygonPoint(newPoint, false))
                                                    
                                                    val newMarker = Marker(mapView).apply {
                                                        position = newPoint
                                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                                        icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_polygon)
                                                        infoWindow = null
                                                        title = "Point ${insertIndex + 1}"
                                                    }
                                                    polygonMarkers.add(insertIndex, newMarker)
                                                    mapView.overlays.add(newMarker)
                                                } else {
                                                    // Ajout normal à la fin si le polygone n'est pas fermé
                                                    Log.d("MapEvents", "Ajout normal à la fin")
                                                    polygonPoints.add(PolygonPoint(newPoint, false))
                                                    
                                                    val newMarker = Marker(mapView).apply {
                                                        position = newPoint
                                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                                        icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_polygon)
                                                        infoWindow = null
                                                        title = "Point ${polygonPoints.size}"
                                                    }
                                                    polygonMarkers.add(newMarker)
                                                    mapView.overlays.add(newMarker)
                                                }

                                                // Mettre à jour le polygone si fermé
                                                if (isPolygonClosed) {
                                                    drawnPolygon?.let { mapView.overlays.remove(it) }
                                                    drawnPolygon = Polygon().apply {
                                                        points = polygonPoints.map { it.point }
                                                        fillColor = AndroidColor.argb(60, 0, 0, 255)
                                                        strokeColor = AndroidColor.BLUE
                                                        strokeWidth = 4f
                                                    }
                                                    mapView.overlays.add(drawnPolygon)
                                                } else {
                                                    drawnPolyline?.let { mapView.overlays.remove(it) }
                                                    if (polygonPoints.size >= 2) {
                                                        drawnPolyline = PolylineOverlay(polygonPoints.map { it.point }).apply {
                                                            strokeColor = AndroidColor.BLUE
                                                            strokeWidth = 4f
                                                        }
                                                        mapView.overlays.add(drawnPolyline)
                                                    }
                                                }
                                                mapView.invalidate()
                                                return true
                                            }
                                        } else {
                                            // Mode point unique
                                            Log.d("MapEvents", "=== MODE POINT UNIQUE ===")
                                            Log.d("MapEvents", "Ancien marqueur: ${selectedMarker?.title}")
                                            Log.d("MapEvents", "Ancienne icône: ${selectedMarker?.icon}")
                                            
                                        selectedLatitude = geoPoint.latitude
                                        selectedLongitude = geoPoint.longitude
                                        
                                        selectedMarker?.let { 
                                                Log.d("MapEvents", "Suppression de l'ancien marqueur")
                                                mapView.overlays.remove(it) 
                                        }
                                        
                                        selectedMarker = Marker(mapView).apply {
                                            position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)
                                            title = "Position sélectionnée"
                                            snippet = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}"
                                            Log.d("MapEvents", "Création du nouveau marqueur")
                                            Log.d("MapEvents", "Nouvelle icône: $icon")
                                        }
                                            
                                            mapView.overlays.add(selectedMarker!!)
                                            Log.d("MapEvents", "Marqueur ajouté à la carte")
                                        mapView.invalidate()
                                            Log.d("MapEvents", "=== FIN MODE POINT UNIQUE ===")
                                        }
                                        return false
                                    }
                                        
                                    override fun onScroll(event: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float, mapView: MapView?): Boolean {
                                        Log.d("MapEvents", "Scroll: dx=$distanceX, dy=$distanceY")
                                        return false
                                    }

                                    override fun onLongPress(e: MotionEvent, mapView: MapView): Boolean {
                                        Log.d("MapEvents", "Long press: x=${e.x}, y=${e.y}")
                                        return false
                                    }
                                })

                                // Ajout de l'overlay pour gérer le recentrage
                                overlays.add(object : Overlay() {
                                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                                        if (modePolygoneActif && polygonPoints.size >= 3 && !isPolygonClosed) {
                                            val (_, _, closureThreshold) = calculateThresholds(mapView)
                                            Log.d("MapEvents", "Seuil de fermeture du polygone: $closureThreshold")
                                            
                                            val projection = mapView.projection
                                            val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                            
                                            val firstPoint = polygonPoints.firstOrNull()
                                            if (firstPoint != null) {
                                                val distance = firstPoint.point.distanceToAsDouble(geoPoint)
                                                Log.d("MapEvents", "Vérification du recentrage - Distance au premier point: $distance")
                                                
                                                if (distance < closureThreshold) {
                                                    Log.d("MapEvents", "Recentrage détecté sur le premier point")
                                                    isPolygonClosed = true
                                                    drawnPolyline?.let { mapView.overlays.remove(it) }
                                                    drawnPolyline = null
                                                    
                                                    drawnPolygon = Polygon().apply {
                                                        points = polygonPoints.map { it.point }
                                                        fillColor = AndroidColor.argb(60, 0, 0, 255)
                                                        strokeColor = AndroidColor.BLUE
                                                        strokeWidth = 4f
                                                    }
                                                    mapView.overlays.add(drawnPolygon)
                                                    mapView.invalidate()
                                        return true
                                                }
                                            }
                                        }
                                        return false
                                    }
                                })

                                // Ajout d'un gestionnaire de zoom
                                overlays.add(object : Overlay() {
                                    override fun onScroll(event: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float, mapView: MapView?): Boolean {
                                        if (isSatelliteView) {
                                            mapView?.invalidate()
                                        }
                                        return false
                                    }

                                    override fun onDoubleTap(e: MotionEvent, mapView: MapView): Boolean {
                                        if (isSatelliteView) {
                                            mapView.invalidate()
                                        }
                                        return false
                                    }
                                })

                                // Ajout du GestureDetector
                                val gestureDetector = android.view.GestureDetector(context, object : android.view.GestureDetector.SimpleOnGestureListener() {
                                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                        Log.d("MapEvents", "=== GESTURE DETECTOR - SINGLE TAP CONFIRMED ===")
                                        Log.d("MapEvents", "Position: x=${e.x}, y=${e.y}")
                                        
                                        if (modePolygoneActif && isPolygonClosed) {
                                            val projection = mapView.projection
                                            val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                            Log.d("MapEvents", "Point géographique: lat=${geoPoint.latitude}, lon=${geoPoint.longitude}")
                                            
                                            // Calculer les seuils
                                            val (_, segmentThreshold, _) = calculateThresholds(mapView)
                                            
                                            // Vérifier si le point est à l'intérieur du polygone
                                            if (isPointInPolygon(geoPoint, polygonPoints.map { it.point })) {
                                                Log.d("MapEvents", "Point à l'intérieur du polygone - insertion")
                                                val newPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                                
                                                // Trouver le segment le plus proche
                                                var minSegmentDistance = Double.MAX_VALUE
                                                var insertIndex = -1
                                                
                                                for (i in 0 until polygonPoints.size) {
                                                    val currentPoint = polygonPoints[i].point
                                                    val nextPoint = polygonPoints[(i + 1) % polygonPoints.size].point
                                                    
                                                    val distanceToSegment = distancePointToSegment(newPoint, currentPoint, nextPoint)
                                                    Log.d("MapEvents", "Distance au segment $i: $distanceToSegment")
                                                    if (distanceToSegment < minSegmentDistance) {
                                                        minSegmentDistance = distanceToSegment
                                                        insertIndex = i + 1
                                                    }
                                                }
                                                
                                                Log.d("MapEvents", "Distance minimale au segment: $minSegmentDistance")
                                                Log.d("MapEvents", "Index d'insertion: $insertIndex")
                                                
                                                if (minSegmentDistance < segmentThreshold) {
                                                    Log.d("MapEvents", "Point trop proche d'un segment existant")
                                                    return true
                                                }
                                                
                                                // Insérer le nouveau point
                                                polygonPoints.add(insertIndex, PolygonPoint(newPoint, false))
                                                
                                                val newMarker = Marker(mapView).apply {
                                                    position = newPoint
                                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                                    icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_polygon)
                                                    infoWindow = null
                                                    title = "Point ${insertIndex + 1}"
                                                }
                                                polygonMarkers.add(insertIndex, newMarker)
                                                mapView.overlays.add(newMarker)
                                                
                                                // Mettre à jour le polygone
                                                drawnPolygon?.let { mapView.overlays.remove(it) }
                                                drawnPolygon = Polygon().apply {
                                                    points = polygonPoints.map { it.point }
                                                    fillColor = AndroidColor.argb(60, 0, 0, 255)
                                                    strokeColor = AndroidColor.BLUE
                                                    strokeWidth = 4f
                                                }
                                                mapView.overlays.add(drawnPolygon)
                                                mapView.invalidate()
                                                
                                                Log.d("MapEvents", "Nouveau point inséré et polygone mis à jour")
                                                return true
                                            }
                                        }
                                        Log.d("MapEvents", "=== FIN GESTURE DETECTOR - SINGLE TAP CONFIRMED ===")
                                        return false
                                    }
                                })

                                // Ajout de l'overlay pour le GestureDetector
                                overlays.add(object : Overlay() {
                                    override fun onTouchEvent(event: MotionEvent, mapView: MapView): Boolean {
                                        Log.d("MapEvents", "=== GESTURE DETECTOR - TOUCH EVENT ===")
                                        Log.d("MapEvents", "Action: ${event.action}")
                                        Log.d("MapEvents", "Position: x=${event.x}, y=${event.y}")
                                        
                                        if (event.action == MotionEvent.ACTION_UP && modePolygoneActif) {
                                            val projection = mapView.projection
                                            val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                                            Log.d("MapEvents", "Point géographique: lat=${geoPoint.latitude}, lon=${geoPoint.longitude}")

                                            return if (isPolygonClosed) {
                                                handleClosedPolygonTouch(geoPoint, mapView)
                                            } else {
                                                handleOpenPolygonTouch(geoPoint, mapView)
                                            }
                                        }
                                        
                                        val result = gestureDetector.onTouchEvent(event)
                                        Log.d("MapEvents", "Résultat du GestureDetector: $result")
                                        Log.d("MapEvents", "=== FIN GESTURE DETECTOR - TOUCH EVENT ===")
                                        return result
                                    }
                                })

                                invalidate()
                                isMapReady = true
                            }
                        } catch (e: Exception) {
                            Log.e("ParcellesBox", "Erreur lors de l'initialisation de la carte", e)
                        }
                    }
                }
            )

            // Bouton de basculement vue carte/satellite
            IconButton(
                onClick = { isSatelliteView = !isSatelliteView },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = if (isSatelliteView) Icons.Default.Map else Icons.Default.Satellite,
                    contentDescription = if (isSatelliteView) "Vue carte" else "Vue satellite"
                )
            }

            // Bouton de basculement mode polygone
            IconButton(
                onClick = { 
                    if (!modePolygoneActif) {
                        // Nettoyer le marqueur unique si on passe en mode polygone
                        selectedMarker?.let { marker ->
                            mapView.overlays.remove(marker)
                            selectedMarker = null
                            mapView.invalidate()
                        }
                    } else {
                        // Nettoyer tous les éléments du mode polygone
                        polygonMarkers.forEach { marker ->
                            mapView.overlays.remove(marker)
                        }
                        polygonMarkers.clear()
                        polygonPoints.clear()
                        drawnPolyline?.let { mapView.overlays.remove(it) }
                        drawnPolyline = null
                        drawnPolygon?.let { mapView.overlays.remove(it) }
                        drawnPolygon = null
                        isPolygonClosed = false
                        polygonPointsCount = 0
                        showHelpText = false
                    }

                    modePolygoneActif = !modePolygoneActif
                    mapView.invalidate()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = if (modePolygoneActif) Icons.Default.Close else Icons.Default.Polyline,
                    contentDescription = if (modePolygoneActif) "Désactiver le mode polygone" else "Activer le mode polygone",
                    tint = if (modePolygoneActif) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Modifiez la condition du bouton d'ajout pour utiliser isPolygonClosed
            if (modePolygoneActif) {
                // Bouton de rafraîchissement
                IconButton(
                    onClick = { 
                        // Nettoyer tous les éléments du mode polygone
                        polygonMarkers.forEach { marker ->
                            mapView.overlays.remove(marker)
                        }
                        polygonMarkers.clear()
                        polygonPoints.clear()
                        drawnPolyline?.let { mapView.overlays.remove(it) }
                        drawnPolyline = null
                        drawnPolygon?.let { mapView.overlays.remove(it) }
                        drawnPolygon = null
                        isPolygonClosed = false
                        polygonPointsCount = 0
                        showHelpText = true
                        mapView.invalidate()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 56.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Réinitialiser le polygone",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = { 
                    if ((modePolygoneActif && isPolygonClosed && polygonPoints.size >= 3) || 
                        (!modePolygoneActif && selectedMarker != null)) {
                        newParcelle = ParcelleInfo("", "", "", 0.0, 0.0)
                        showParcelleDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                    .alpha(
                        if (modePolygoneActif) {
                            if (isPolygonClosed && polygonPoints.size >= 3) 1f else 0.3f
                        } else {
                            if (selectedMarker != null) 1f else 0.3f
                        }
                    ),
                enabled = if (modePolygoneActif) {
                    isPolygonClosed && polygonPoints.size >= 3
                } else {
                    selectedMarker != null
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (modePolygoneActif) 
                        "Ajouter une parcelle (${polygonPoints.size} points)" 
                    else 
                        "Ajouter une parcelle"
                )
            }

            // Modifier la position du texte d'aide
            if (showHelpText) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "3 points minimum requis pour créer une parcelle",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Liste des parcelles (hauteur fixe de 200 dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (parcelles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune parcelle enregistrée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parcelles) { parcelle ->
                        var showDeleteConfirmation by remember { mutableStateOf(false) }
                        val dismissState = rememberSwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            positionalThreshold = { it * 0.5f },
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    showDeleteConfirmation = true
                                    false
                                } else {
                                    false
                                }
                            }
                        )
                        val coroutineScope = rememberCoroutineScope()

                        if (showDeleteConfirmation) {
                            AlertDialog(
                                onDismissRequest = { 
                                    showDeleteConfirmation = false
                                    coroutineScope.launch {
                                        dismissState.reset()
                                    }
                                },
                                title = { Text("Confirmer la suppression") },
                                text = { Text("Voulez-vous vraiment supprimer la parcelle \"${parcelle.name}\" ?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteConfirmation = false
                                            deleteParcelle(parcelle)
                                            coroutineScope.launch {
                                                dismissState.reset()
                                            }
                                        }
                                    ) {
                                        Text("Supprimer")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { 
                                            showDeleteConfirmation = false
                                            coroutineScope.launch {
                                                dismissState.reset()
                                            }
                                        }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.surface
                                }
                                val alignment = Alignment.CenterEnd
                                val icon = Icons.Default.Delete

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                }
                            },
                            content = {
                                var isPressed by remember { mutableStateOf(false) }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(parcelle) {
                                            detectTapGestures(
                                                onPress = {
                                                    isPressed = true
                                                    tryAwaitRelease()
                                                    isPressed = false
                                                },
                                                onTap = {
                                                    // Simple clic : centrer sur la parcelle et mettre à jour la sélection
                                                    if (!modePolygoneActif) {
                                                        selectedParcelleId = parcelle.id
                                                        if (parcelle.polygonPoints.isNotEmpty()) {
                                                            // Calculer la boîte englobante du polygone
                                                            var minLat = Double.MAX_VALUE
                                                            var maxLat = Double.MIN_VALUE
                                                            var minLon = Double.MAX_VALUE
                                                            var maxLon = Double.MIN_VALUE
                                                            
                                                            parcelle.polygonPoints.forEach { point ->
                                                                minLat = minOf(minLat, point.latitude)
                                                                maxLat = maxOf(maxLat, point.latitude)
                                                                minLon = minOf(minLon, point.longitude)
                                                                maxLon = maxOf(maxLon, point.longitude)
                                                            }
                                                            
                                                            // Ajouter une marge de 10%
                                                            val latMargin = (maxLat - minLat) * 0.9
                                                            val lonMargin = (maxLon - minLon) * 0.9

                                                            val boundingBox = org.osmdroid.util.BoundingBox(
                                                                maxLat + latMargin,
                                                                maxLon + lonMargin,
                                                                minLat - latMargin,
                                                                minLon - lonMargin
                                                            )
                                                            mapView.zoomToBoundingBox(boundingBox, true, 15)
                                                        } else {
                                                            // Pour une parcelle avec un seul point
                                                            mapView.controller.animateTo(
                                                                GeoPoint(parcelle.latitude, parcelle.longitude),
                                                                17.0,
                                                                500L
                                                            )
                                                        }
                                                    }
                                                },
                                                onDoubleTap = {
                                                    // Double clic : ouvrir le dialogue de modification
                                                    if (!modePolygoneActif) {
                                                        parcelleToEdit = parcelle
                                                        newParcelle = ParcelleInfo(
                                                            name = parcelle.name,
                                                            surface = parcelle.surface.toString(),
                                                            cepage = parcelle.cepage,
                                                            latitude = parcelle.latitude,
                                                            longitude = parcelle.longitude
                                                        )
                                                        selectedLatitude = parcelle.latitude
                                                        selectedLongitude = parcelle.longitude
                                                        
                                                        isEditingParcelle = true
                                                        showParcelleDialog = true
                                                    }
                                                }
                                            )
                                        },
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp,
                                    shadowElevation = 2.dp,
                                    border = if (selectedParcelleId == parcelle.id)
                                        FoundationBorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                                    else
                                        null,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = parcelle.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${parcelle.surface} ha",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = parcelle.cepage,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            },
                            enableDismissFromEndToStart = true,
                            enableDismissFromStartToEnd = false
                        )
                    }
                }
            }
        }
    }

    // Effet pour centrer la carte de manière asynchrone
    LaunchedEffect(parcelleToEdit) {
        parcelleToEdit?.let { parcelle ->
            if (!modePolygoneActif) {
                // Centrer la carte sur la parcelle
                if (parcelle.polygonPoints.isNotEmpty()) {
                    // Calculer la boîte englobante du polygone
                    var minLat = Double.MAX_VALUE
                    var maxLat = Double.MIN_VALUE
                    var minLon = Double.MAX_VALUE
                    var maxLon = Double.MIN_VALUE
                    
                    parcelle.polygonPoints.forEach { point ->
                        minLat = minOf(minLat, point.latitude)
                        maxLat = maxOf(maxLat, point.latitude)
                        minLon = minOf(minLon, point.longitude)
                        maxLon = maxOf(maxLon, point.longitude)
                    }
                    
                    // Ajouter une marge de 10%
                    val latMargin = (maxLat - minLat) * 1.7
                    val lonMargin = (maxLon - minLon) * 1.7

                    val boundingBox = org.osmdroid.util.BoundingBox(
                        maxLat + latMargin,
                        maxLon + lonMargin,
                        minLat - latMargin,
                        minLon - lonMargin
                    )
                    mapView.zoomToBoundingBox(boundingBox, true)
                } else {
                    // Pour une parcelle avec un seul point, centrer directement dessus
                    mapView.controller.animateTo(
                        GeoPoint(parcelle.latitude, parcelle.longitude),
                        17.0,// Niveau de zoom
                        1500L // Durée de l'animation en ms
                    )
                }
                
                // Si la parcelle a un polygone, on ne l'affiche pas lors de la modification
                // Note: Ce bloc était précédemment dans le clickable, il est déplacé ici
                // pour s'assurer qu'il s'exécute après le centrage.
                // Cependant, il semble qu'il nettoie les polygones de la carte ce qui pourrait
                // être contre-intuitif si on veut voir le polygone sur lequel on a centré.
                // À revoir si le comportement est celui attendu.
                if (parcelle.polygonPoints.isNotEmpty()) {
                    // Nettoyer les points et marqueurs existants (du mode édition polygone actif)
                    polygonMarkers.forEach { marker ->
                        mapView.overlays.remove(marker)
                    }
                    polygonMarkers.clear()
                    polygonPoints.clear()
                    drawnPolyline?.let { mapView.overlays.remove(it) }
                    drawnPolyline = null
                    // On ne nettoie pas drawnPolygon ici car il pourrait s'agir du polygone de la parcelle
                    mapView.invalidate()
                }
            }
        }
    }
}

@Composable
fun InformationItem(
    information: InformationsGenerales,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ces informations ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = information.nomDomaine,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${information.modeCulture} • ${information.surfaceTotale} ha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Code postal: ${information.codePostal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (information.certifications.isNotEmpty()) {
                    Text(
                        text = "Certifications: ${information.certifications.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun OperateurItem(
    operateur: Operateur,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cet opérateur ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = operateur.nom,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (operateur.disponibleWeekend) {
                        Text(
                            text = "Disponible le week-end",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (operateur.diplomes.contains("CertiPhyto")) {
                        Text(
                            text = "CertiPhyto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (operateur.materielMaitrise.isNotEmpty()) {
                    Text(
                        text = "Matériel maîtrisé: ${operateur.materielMaitrise.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PulverisateurItem(
    pulverisateur: Pulverisateur,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ce pulvérisateur ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pulverisateur.nomMateriel,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Mode: ${pulverisateur.modeDeplacement}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type buses: ${pulverisateur.typeBuses}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Largeur: ${pulverisateur.largeurTraitement}m • Volume: ${pulverisateur.volumeTotalCuve}L",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            Configuration.getInstance().userAgentValue = context.packageName
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    return mapView
} 

// Ajoutez cette fonction utilitaire pour calculer la distance d'un point à un segment
private fun distancePointToSegment(point: GeoPoint, segmentStart: GeoPoint, segmentEnd: GeoPoint): Double {
    Log.d("MapEvents", "=== CALCUL DISTANCE POINT-SEGMENT ===")
    Log.d("MapEvents", "Point cliqué: lat=${point.latitude}, lon=${point.longitude}")
    Log.d("MapEvents", "Début segment: lat=${segmentStart.latitude}, lon=${segmentStart.longitude}")
    Log.d("MapEvents", "Fin segment: lat=${segmentEnd.latitude}, lon=${segmentEnd.longitude}")
    
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
        Log.d("MapEvents", "Paramètre de projection: $param")
    }

    var xx: Double
    var yy: Double

    if (param < 0) {
        xx = x1
        yy = y1
        Log.d("MapEvents", "Point projeté sur début du segment")
    } else if (param > 1) {
        xx = x2
        yy = y2
        Log.d("MapEvents", "Point projeté sur fin du segment")
    } else {
        xx = x1 + param * C
        yy = y1 + param * D
        Log.d("MapEvents", "Point projeté sur le segment")
    }

    val dx = x - xx
    val dy = y - yy
    val distance = sqrt(dx * dx + dy * dy)
    Log.d("MapEvents", "Distance calculée: $distance")
    Log.d("MapEvents", "=== FIN CALCUL DISTANCE ===")
    return distance
}

// Ajoutez cette classe après les imports
class PolylineOverlay(points: List<GeoPoint>) : Overlay() {
    private val points = points.toMutableList()
    var strokeColor = AndroidColor.BLUE
    var strokeWidth = 4f

    override fun draw(canvas: android.graphics.Canvas?, mapView: MapView?, shadow: Boolean) {
        if (canvas == null || mapView == null || shadow || points.size < 2) return

        val paint = android.graphics.Paint().apply {
            color = strokeColor
            strokeWidth = this@PolylineOverlay.strokeWidth
            style = android.graphics.Paint.Style.STROKE
            isAntiAlias = true
        }

        val path = android.graphics.Path()
        var first = true
        points.forEach { point ->
            val screenPoint = mapView.projection.toPixels(point, null)
            if (first) {
                path.moveTo(screenPoint.x.toFloat(), screenPoint.y.toFloat())
                first = false
            } else {
                path.lineTo(screenPoint.x.toFloat(), screenPoint.y.toFloat())
            }
        }

        canvas.drawPath(path, paint)
    }
}

// Modifier la fonction isPointInPolygon pour accepter GeoPoint
fun isPointInPolygon(point: IGeoPoint, polygon: List<GeoPoint>): Boolean {
    var inside = false
    var j = polygon.size - 1
    
    for (i in polygon.indices) {
        if ((polygon[i].longitude > point.longitude) != (polygon[j].longitude > point.longitude) &&
            (point.latitude < (polygon[j].latitude - polygon[i].latitude) * 
            (point.longitude - polygon[i].longitude) / 
            (polygon[j].longitude - polygon[i].longitude) + polygon[i].latitude)) {
            inside = !inside
        }
        j = i
    }
    
    return inside
} 

