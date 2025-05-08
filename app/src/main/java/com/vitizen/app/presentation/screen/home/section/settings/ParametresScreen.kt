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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.material.icons.filled.MyLocation
import com.google.accompanist.permissions.isGranted

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

// Ajout des couleurs pour les marqueurs
private val markerColors = listOf(
    android.graphics.Color.RED,
    android.graphics.Color.BLUE,
    android.graphics.Color.GREEN,
    android.graphics.Color.MAGENTA,
    android.graphics.Color.CYAN,
    android.graphics.Color.YELLOW,
    android.graphics.Color.DKGRAY
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ParcellesBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // États
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.SATELLITE)) }
    var showParcelleDialog by remember { mutableStateOf(false) }
    var newParcelle by remember { mutableStateOf(ParcelleInfo("", "", "", 0.0, 0.0)) }
    var isEditingParcelle by remember { mutableStateOf(false) }
    var parcelleToEdit by remember { mutableStateOf<Parcelle?>(null) }
    var parcelleToDelete by remember { mutableStateOf<Parcelle?>(null) }
    
    // État des parcelles
    val parcelles by viewModel.parcelles.collectAsState()

    // Position de la caméra
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(46.603354, 1.888334), // Position par défaut France
            15f
        )
    }

    // Ajout de l'état pour la position actuelle
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Vérification des permissions de localisation
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Fonction pour obtenir la position actuelle
    fun getCurrentLocation() {
        if (locationPermissionState.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            currentLocation!!,
                            15f
                        )
                    }
                }
            } catch (e: SecurityException) {
                // Gérer l'erreur de permission
            }
                            } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties.copy(
                    isMyLocationEnabled = locationPermissionState.status.isGranted
                ),
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                    newParcelle = newParcelle.copy(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                }
            ) {
                // Affichage des parcelles existantes
                parcelles.forEachIndexed { index, parcelle ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(parcelle.latitude, parcelle.longitude)
                        ),
                        title = parcelle.name,
                        snippet = "${parcelle.surface} ha • ${parcelle.cepage}",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED + (index * 30f % 360f)
                        )
                    )
                }

                // Marqueur de sélection
                selectedLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Position sélectionnée",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_BLUE
                        )
                    )
                }
            }

            // Bouton de basculement vue satellite/normale
            IconButton(
                onClick = {
                    mapProperties = mapProperties.copy(
                        mapType = if (mapProperties.mapType == MapType.SATELLITE)
                            MapType.NORMAL else MapType.SATELLITE
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = if (mapProperties.mapType == MapType.NORMAL) 
                        Icons.Default.Satellite else Icons.Default.Map,
                    contentDescription = "Changer le type de carte"
                )
            }

            // Bouton d'ajout de parcelle
            IconButton(
                onClick = { 
                    if (selectedLatLng != null) {
                        showParcelleDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                    .alpha(if (selectedLatLng != null) 1f else 0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une parcelle"
                )
            }

            // Ajout du bouton de géolocalisation
            IconButton(
                onClick = { getCurrentLocation() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .offset(y = 56.dp) // Pour le placer sous le bouton de vue satellite
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ma position",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Liste des parcelles (hauteur fixe de 200dp)
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
                                    selectedLatLng = LatLng(parcelle.latitude, parcelle.longitude)
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
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = parcelle.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${parcelle.surface} ha • ${parcelle.cepage}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { parcelleToDelete = parcelle }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog d'ajout/modification de parcelle
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
                        if (newParcelle.name.isNotBlank() && 
                            newParcelle.surface.isNotBlank() && 
                            newParcelle.cepage.isNotBlank() &&
                            selectedLatLng != null
                        ) {
                            if (isEditingParcelle && parcelleToEdit != null) {
                                viewModel.updateParcelle(
                                    parcelleToEdit!!.copy(
                                        name = newParcelle.name,
                                        surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                        cepage = newParcelle.cepage,
                                        latitude = selectedLatLng!!.latitude,
                                        longitude = selectedLatLng!!.longitude
                                    )
                                )
                            } else {
                                viewModel.addParcelle(
                                    Parcelle(
                                        id = System.currentTimeMillis().toString(),
                                        name = newParcelle.name,
                                        surface = newParcelle.surface.toDoubleOrNull() ?: 0.0,
                                        cepage = newParcelle.cepage,
                                        latitude = selectedLatLng!!.latitude,
                                        longitude = selectedLatLng!!.longitude,
                                        typeConduite = "",
                                        largeur = 0.0,
                                        hauteur = 0.0
                                    )
                                )
                            }
                            showParcelleDialog = false
                            isEditingParcelle = false
                            parcelleToEdit = null
                            selectedLatLng = null
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

    // Dialog de confirmation de suppression
    if (parcelleToDelete != null) {
        AlertDialog(
            onDismissRequest = { parcelleToDelete = null },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cette parcelle ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        parcelleToDelete?.let { parcelle ->
                            viewModel.deleteParcelle(parcelle)
                            parcelleToDelete = null
                        }
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { parcelleToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
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

