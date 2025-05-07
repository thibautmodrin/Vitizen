package com.vitizen.app.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

private const val TAG = "OsmMapPicker"

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
    onLocationSelected: (Double, Double) -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélectionner un emplacement - $postalCode") },
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
                    Button(
                        onClick = {
                            onLocationSelected(selectedLatitude, selectedLongitude)
                            onDismiss()
                        }
                    ) {
                        Text("Valider")
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
                                    overlayManager.add(MyLocationNewOverlay(GpsMyLocationProvider(context), this).apply {
                                        enableMyLocation()
                                        enableFollowLocation()
                                    })
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
                                })

                                // Ajout du marqueur initial si des coordonnées sont fournies
                                if (initialLatitude != 0.0 && initialLongitude != 0.0) {
                                    selectedMarker = Marker(this).apply {
                                        position = GeoPoint(initialLatitude, initialLongitude)
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                                        title = "Position sélectionnée"
                                        snippet = "Lat: $initialLatitude, Lon: $initialLongitude"
                                    }
                                    overlayManager.add(selectedMarker!!)
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