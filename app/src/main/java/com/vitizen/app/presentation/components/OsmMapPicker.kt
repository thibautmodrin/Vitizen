package com.vitizen.app.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.vitizen.app.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.Overlay
import java.io.File
import androidx.compose.ui.graphics.RectangleShape
import android.os.Handler
import android.os.Looper

private const val TAG = "OsmMapPicker"

data class ParcelleInfo(
    val name: String,
    val surface: String,
    val cepage: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsmMapPicker(
    initialLatitude: Double = 46.603354,
    initialLongitude: Double = 1.888334,
    postalCode: String = "",
    onLocationSelected: (List<ParcelleInfo>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isMapReady by remember { mutableStateOf(false) }
    var selectedMarker: Marker? by remember { mutableStateOf(null) }
    var isSatelliteView by remember { mutableStateOf(false) }
    var selectedLatitude by remember { mutableStateOf(initialLatitude) }
    var selectedLongitude by remember { mutableStateOf(initialLongitude) }
    var showParcelleDialog by remember { mutableStateOf(false) }
    var parcelleName by remember { mutableStateOf("") }
    var parcelleSurface by remember { mutableStateOf("") }
    var parcelleCepage by remember { mutableStateOf("") }
    var parcelles by remember { mutableStateOf(listOf<ParcelleInfo>()) }
    var myLocationOverlay: MyLocationNewOverlay? by remember { mutableStateOf(null) }
    var showDoubleClickDialog by remember { mutableStateOf(false) }
    var doubleClickLatitude by remember { mutableStateOf(0.0) }
    var doubleClickLongitude by remember { mutableStateOf(0.0) }
    var parcelleMarkers by remember { mutableStateOf(listOf<Marker>()) }

    DisposableEffect(Unit) {
        // Utilisation du stockage interne de l'application
        val osmdroidDir = File(context.filesDir, "osmdroid").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val tilesDir = File(osmdroidDir, "tiles").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        try {
            val config = Configuration.getInstance()
            config.apply {
                userAgentValue = context.packageName
                osmdroidBasePath = osmdroidDir
                osmdroidTileCache = tilesDir
                gpsWaitTime = 1000L
                tileFileSystemCacheMaxBytes = 8L * 1024L * 1024L // 8MB
                tileFileSystemThreads = 8
                tileDownloadThreads = 8
                tileFileSystemCacheTrimBytes = 4L * 1024L * 1024L // 4MB
                tileDownloadMaxQueueSize = 8
                tileFileSystemMaxQueueSize = 16
            }

            Log.d(TAG, "Configuration d'Osmdroid")
            Log.d(TAG, "Cache directory: ${tilesDir.absolutePath}")
            Log.d(TAG, "Configuration terminée: userAgent=${config.userAgentValue}, cacheSize=${config.tileFileSystemCacheMaxBytes}")

            // Vérification de l'accessibilité du cache
            if (!tilesDir.canWrite()) {
                Log.e(TAG, "Le répertoire de cache n'est pas accessible en écriture")
                // Tentative de correction des permissions
                tilesDir.setWritable(true, false)
                if (!tilesDir.canWrite()) {
                    Log.e(TAG, "Impossible de rendre le répertoire de cache accessible en écriture")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la configuration d'Osmdroid", e)
        }

        onDispose {
            try {
                mapView.onDetach()
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors du détachement de la carte", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "Permission de localisation: $hasLocationPermission")
    }

    if (showParcelleDialog) {
        AlertDialog(
            onDismissRequest = { showParcelleDialog = false },
            title = { Text("Définir la parcelle") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = parcelleName,
                        onValueChange = { parcelleName = it },
                        label = { Text("Nom de la parcelle") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parcelleSurface,
                        onValueChange = { parcelleSurface = it },
                        label = { Text("Surface (ha)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parcelleCepage,
                        onValueChange = { parcelleCepage = it },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newParcelle = ParcelleInfo(
                            name = parcelleName,
                            surface = parcelleSurface,
                            cepage = parcelleCepage,
                            latitude = selectedLatitude,
                            longitude = selectedLongitude
                        )
                        parcelles = parcelles + newParcelle
                        parcelleName = ""
                        parcelleSurface = ""
                        parcelleCepage = ""
                        showParcelleDialog = false
                    }
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showParcelleDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showDoubleClickDialog) {
        AlertDialog(
            onDismissRequest = { showDoubleClickDialog = false },
            title = { Text("Position sélectionnée") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Latitude: ${doubleClickLatitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Longitude: ${doubleClickLongitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = parcelleName,
                        onValueChange = { parcelleName = it },
                        label = { Text("Nom de la parcelle") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parcelleSurface,
                        onValueChange = { parcelleSurface = it },
                        label = { Text("Surface (ha)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = parcelleCepage,
                        onValueChange = { parcelleCepage = it },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newParcelle = ParcelleInfo(
                            name = parcelleName,
                            surface = parcelleSurface,
                            cepage = parcelleCepage,
                            latitude = doubleClickLatitude,
                            longitude = doubleClickLongitude
                        )
                        parcelles = parcelles + newParcelle
                        
                        // Création du marker pour la nouvelle parcelle
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(doubleClickLatitude, doubleClickLongitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                            title = parcelleName
                            snippet = "${parcelleSurface} ha - ${parcelleCepage}"
                        }
                        mapView.overlayManager.add(marker)
                        parcelleMarkers = parcelleMarkers + marker
                        
                        parcelleName = ""
                        parcelleSurface = ""
                        parcelleCepage = ""
                        showDoubleClickDialog = false
                    }
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDoubleClickDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ajouter des parcelles - $postalCode") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSatelliteView = !isSatelliteView }) {
                                Icon(
                                    imageVector = if (isSatelliteView) Icons.Default.Map else Icons.Default.Satellite,
                                    contentDescription = if (isSatelliteView) "Vue carte" else "Vue satellite"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Column {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Parcelles ajoutées (${parcelles.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    items(parcelles) { parcelle ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 0.5.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            ),
                                            shape = RectangleShape
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        parcelle.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        maxLines = 1,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        "${parcelle.surface} ha",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        parcelle.cepage,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        // Suppression du marker associé
                                                        parcelleMarkers.find { it.position.latitude == parcelle.latitude && it.position.longitude == parcelle.longitude }?.let { marker ->
                                                            mapView.overlayManager.remove(marker)
                                                            parcelleMarkers = parcelleMarkers.filter { it != marker }
                                                        }
                                                        parcelles = parcelles.filter { it != parcelle }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Supprimer",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lat: %.6f, Lon: %.6f".format(selectedLatitude, selectedLongitude),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Row {
                                    Button(
                                        onClick = { showParcelleDialog = true },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("Définir")
                                    }
                                    Button(
                                        onClick = {
                                            onLocationSelected(parcelles)
                                            onDismiss()
                                        },
                                        enabled = parcelles.isNotEmpty()
                                    ) {
                                        Text("Valider")
                                    }
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AndroidView(
                        factory = { mapView },
                        modifier = Modifier.fillMaxSize(),
                        update = { view ->
                            if (!isMapReady) {
                                try {
                                    view.apply {
                                        setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(true)
                                        controller.setZoom(15.0)
                                        controller.setCenter(GeoPoint(initialLatitude, initialLongitude))

                                        // Configuration des contrôles de zoom
                                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                                        Log.d(TAG, "Contrôles de zoom configurés")

                                        // Ajout de la boussole
                                        overlayManager.add(CompassOverlay(context, this).apply {
                                            enableCompass()
                                        })
                                        Log.d(TAG, "Boussole ajoutée")

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
                                            overlayManager.add(myLocationOverlay!!)
                                            Log.d(TAG, "Overlay de localisation ajouté")
                                        }

                                        // Ajout de l'overlay de sélection
                                        overlayManager.add(object : Overlay() {
                                            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                                                val projection = mapView.projection
                                                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                                
                                                // Mise à jour des coordonnées sélectionnées
                                                selectedLatitude = geoPoint.latitude
                                                selectedLongitude = geoPoint.longitude
                                                
                                                // Suppression de l'ancien marqueur
                                                selectedMarker?.let { overlayManager.remove(it) }
                                                
                                                // Création du nouveau marqueur
                                                selectedMarker = Marker(mapView).apply {
                                                    position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                    icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                                                    title = "Position sélectionnée"
                                                    snippet = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}"
                                                }
                                                overlayManager.add(selectedMarker!!)
                                                
                                                return true
                                            }

                                            override fun onDoubleTap(e: MotionEvent, mapView: MapView): Boolean {
                                                val projection = mapView.projection
                                                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                                                
                                                doubleClickLatitude = geoPoint.latitude
                                                doubleClickLongitude = geoPoint.longitude
                                                showDoubleClickDialog = true
                                                
                                                return true
                                            }
                                        })

                                        // Ajout des marqueurs pour les parcelles existantes
                                        parcelles.forEach { parcelle ->
                                            val marker = Marker(this).apply {
                                                position = GeoPoint(parcelle.latitude, parcelle.longitude)
                                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                                                title = parcelle.name
                                                snippet = "${parcelle.surface} ha - ${parcelle.cepage}"
                                            }
                                            overlayManager.add(marker)
                                        }

                                        invalidate()
                                        isMapReady = true
                                        Log.d(TAG, "Carte initialisée avec zoom=15.0 et centre=$initialLatitude,$initialLongitude")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Erreur lors de l'initialisation de la carte", e)
                                }
                            } else {
                                try {
                                    // Mise à jour de la source des tuiles si la vue satellite change
                                    view.setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
                                    view.invalidate()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Erreur lors de l'invalidation de la carte", e)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
} 