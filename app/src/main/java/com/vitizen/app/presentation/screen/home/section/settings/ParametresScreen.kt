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
import androidx.compose.ui.graphics.Color
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
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.util.Log
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
import com.vitizen.app.R

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToForm: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
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
    var selectedLatitude by remember { mutableStateOf(46.603354) }
    var selectedLongitude by remember { mutableStateOf(1.888334) }
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
    var polygonPointsCount by remember { mutableStateOf(0) }
    var showHelpText by remember { mutableStateOf(false) }
    var isPolygonClosed by remember { mutableStateOf(false) }
    var drawnPolygon: Polygon? by remember { mutableStateOf(null) }

    // Ajouter l'état pour stocker les polygones des parcelles
    var parcellePolygons by remember { mutableStateOf(mapOf<String, Polygon>()) }

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

    // Remplacer les LaunchedEffect existants par une meilleure gestion des états
    LaunchedEffect(modePolygoneActif, polygonPoints) {
        polygonPointsCount = polygonPoints.size
        showHelpText = modePolygoneActif && polygonPoints.size < 3
        }

    LaunchedEffect(Unit) {
        hasLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(isSatelliteView, parcelles, isMapReady) {
        if (isMapReady) {
            try {
                // Mise à jour de la vue satellite
                mapView.setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
                
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
                Log.e("ParcellesBox", "Erreur lors de la mise à jour de la carte", e)
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
                                selectedMarker?.let { marker ->
                                    marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_gray)
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
                                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

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
                                            if (minDistance < 100) {
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
                                                if (closestPointIndex == 0 && !isPolygonClosed && polygonPoints.size > 1) {
                                                    Log.d("MapEvents", "Impossible de supprimer le premier point - polygone non fermé et plus d'un point")
                                                    return true
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
                                            
                                            if (minDistance < 10) {
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
                                                        icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                                                        infoWindow = null
                                                        title = "Point 1"
                                                    }
                                                    polygonMarkers.add(newMarker)
                                                    mapView.overlays.add(newMarker)
                                                } else if (isPolygonClosed) {
                                                    // Mode insertion uniquement si le polygone est fermé
                                                    Log.d("MapEvents", "Mode insertion - polygone fermé")
                                                    var minDistance = Double.MAX_VALUE
                                                    var insertIndex = -1
                                                    
                                                    for (i in 0 until polygonPoints.size) {
                                                        val currentPoint = polygonPoints[i].point
                                                        val nextPoint = polygonPoints[(i + 1) % polygonPoints.size].point
                                                        
                                                        val distance = distancePointToSegment(newPoint, currentPoint, nextPoint)
                                                        Log.d("MapEvents", "Distance au segment $i: $distance")
                                                        
                                                        if (distance < minDistance) {
                                                            minDistance = distance
                                                            insertIndex = i + 1
                                                        }
                                                    }
                                                    
                                                    Log.d("MapEvents", "Insertion du point à l'index $insertIndex")
                                                    polygonPoints.add(insertIndex, PolygonPoint(newPoint, false))
                                                    
                                                    val newMarker = Marker(mapView).apply {
                                                        position = newPoint
                                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                                        icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
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
                                                        icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
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
                                            selectedLatitude = geoPoint.latitude
                                            selectedLongitude = geoPoint.longitude

                                            selectedMarker?.let { mapView.overlays.remove(it) }
                                        selectedMarker = Marker(mapView).apply {
                                            position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)
                                            title = "Position sélectionnée"
                                            snippet = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}"
                                        }
                                            mapView.overlays.add(selectedMarker!!)
                                        mapView.invalidate()
                                        return true
                                        }
                                        
                                        Log.d("MapEvents", "=== FIN ÉVÉNEMENT SINGLE TAP ===")
                                        return false
                                    }

                                    override fun onScroll(event: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float, mapView: MapView?): Boolean {
                                        Log.d("MapEvents", "Scroll: dx=$distanceX, dy=$distanceY")
                                        return false
                                    }

                                    override fun onDoubleTap(e: MotionEvent, mapView: MapView): Boolean {
                                        Log.d("MapEvents", "Double tap: x=${e.x}, y=${e.y}")
                                        return false
                                    }

                                    override fun onLongPress(e: MotionEvent, mapView: MapView): Boolean {
                                        Log.d("MapEvents", "Long press: x=${e.x}, y=${e.y}")
                                        return false
                                    }
                                })

                                // Ajout d'un overlay pour gérer le recentrage
                                overlays.add(object : Overlay() {
                                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                                        if (modePolygoneActif && polygonPoints.size >= 3 && !isPolygonClosed) {
                                            val projection = mapView.projection
                                            val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                            
                                            // Vérifier si on clique sur le premier point
                                            val firstPoint = polygonPoints.firstOrNull()
                                            if (firstPoint != null) {
                                                val distance = firstPoint.point.distanceToAsDouble(geoPoint)
                                                Log.d("MapEvents", "Vérification du recentrage - Distance au premier point: $distance")
                                                
                                                if (distance < 100) {
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
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
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
                                    
                                    // Si la parcelle a un polygone, on ne l'affiche pas lors de la modification
                                    if (parcelle.polygonPoints.isNotEmpty()) {
                                        // Nettoyer les points et marqueurs existants
                                        polygonMarkers.forEach { marker ->
                                            mapView.overlays.remove(marker)
                                        }
                                        polygonMarkers.clear()
                                        polygonPoints.clear()
                                        drawnPolyline?.let { mapView.overlays.remove(it) }
                                        drawnPolyline = null
                                        mapView.invalidate()
                                    }
                                    
                                    isEditingParcelle = true
                                    showParcelleDialog = true
                                },
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
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
                                        style = MaterialTheme.typography.titleMedium
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
                                IconButton(
                                    onClick = { deleteParcelle(parcelle) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
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

