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
import com.vitizen.app.presentation.navigation.Screen
import com.vitizen.app.presentation.components.OsmMapPicker
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
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.views.CustomZoomButtonsController
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import org.osmdroid.events.ZoomEvent
import java.io.File

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

    LaunchedEffect(Unit) {
        hasLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Effet pour mettre à jour la vue satellite
    LaunchedEffect(isSatelliteView) {
        if (isMapReady) {
            try {
                mapView.setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
                mapView.invalidate()
            } catch (e: Exception) {
                Log.e("ParcellesBox", "Erreur lors du changement de vue", e)
            }
        }
    }

    // Effet pour mettre à jour les marqueurs des parcelles
    LaunchedEffect(parcelles) {
        if (isMapReady) {
            // Supprimer les anciens marqueurs
            parcelleMarkers.forEach { marker ->
                mapView.overlays.remove(marker)
            }
            parcelleMarkers = emptyList()

            // Ajouter les nouveaux marqueurs
            val newMarkers = parcelles.map { parcelle ->
                Marker(mapView).apply {
                    position = GeoPoint(parcelle.latitude, parcelle.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                    title = parcelle.name
                    snippet = "${parcelle.surface} ha • ${parcelle.cepage}"
                }
            }
            parcelleMarkers = newMarkers
            newMarkers.forEach { marker ->
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
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
                                // Mise à jour d'une parcelle existante
                                val updatedParcelle = parcelleToEdit!!.copy(
                                    name = newParcelle.name,
                                    surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                    cepage = newParcelle.cepage,
                                    latitude = selectedLatitude,
                                    longitude = selectedLongitude
                                )
                                viewModel.updateParcelle(updatedParcelle)
                            } else {
                                // Création d'une nouvelle parcelle
                                val parcelleToAdd = Parcelle(
                                    id = System.currentTimeMillis().toString(),
                                    name = newParcelle.name,
                                    surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                    cepage = newParcelle.cepage,
                                    latitude = selectedLatitude,
                                    longitude = selectedLongitude,
                                    typeConduite = "",
                                    largeur = 0.0,
                                    hauteur = 0.0
                                )
                                viewModel.addParcelle(parcelleToAdd)
                            }
                            showParcelleDialog = false
                            isEditingParcelle = false
                            parcelleToEdit = null
                            // Réinitialiser le formulaire
                            newParcelle = ParcelleInfo("", "", "", 0.0, 0.0)
                            // Supprimer le marqueur de sélection
                            selectedMarker?.let { mapView.overlays.remove(it) }
                            selectedMarker = null
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
                                        enableFollowLocation()
                                        runOnFirstFix {
                                            val myLocation = myLocation
                                            if (myLocation != null) {
                                                Handler(Looper.getMainLooper()).post {
                                                    controller.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))
                                                    controller.setZoom(15.0)
                                                }
                                            }
                                        }
                                    }
                                    overlays.add(myLocationOverlay!!)
                                }

                                // Ajout de l'overlay de sélection
                                overlays.add(object : Overlay() {
                                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                                        val projection = mapView.projection
                                        val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                        
                                        // Mise à jour immédiate des coordonnées
                                        selectedLatitude = geoPoint.latitude
                                        selectedLongitude = geoPoint.longitude
                                        
                                        // Suppression immédiate de l'ancien marqueur
                                        selectedMarker?.let { 
                                            overlays.remove(it)
                                            selectedMarker = null
                                        }
                                        
                                        // Création et ajout immédiat du nouveau marqueur
                                        selectedMarker = Marker(mapView).apply {
                                            position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                                            title = "Position sélectionnée"
                                            snippet = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}"
                                        }
                                        overlays.add(selectedMarker!!)
                                        mapView.invalidate()
                                        
                                        return true
                                    }

                                    override fun onDoubleTap(e: MotionEvent, mapView: MapView): Boolean {
                                        if (selectedMarker != null) {
                                            showParcelleDialog = true
                                        }
                                        return true
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

            // Bouton d'ajout de parcelle (toujours visible)
            IconButton(
                onClick = { 
                    if (selectedMarker != null) {
                        showParcelleDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                    .alpha(if (selectedMarker != null) 1f else 0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une parcelle"
                )
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
                                    onClick = { viewModel.deleteParcelle(parcelle) },
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

