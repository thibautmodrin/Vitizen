package com.vitizen.app.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File

private const val TAG = "OsmMapPicker"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsmMapPicker(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    postalCode: String = "",
    onLocationSelected: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Configuration d'Osmdroid avant tout
    DisposableEffect(Unit) {
        Log.d(TAG, "Configuration d'Osmdroid")
        val osmConfig = Configuration.getInstance()
        
        // Utilisation du stockage externe
        val osmdroidDir = File(context.getExternalFilesDir(null), "osmdroid").apply { 
            if (!exists()) {
                mkdirs()
            }
        }
        Log.d(TAG, "Cache directory: ${osmdroidDir.absolutePath}")
        
        osmConfig.apply {
            userAgentValue = context.packageName
            osmdroidTileCache = osmdroidDir
            osmdroidBasePath = osmdroidDir
            gpsWaitTime = 2000L
            tileFileSystemCacheMaxBytes = 1024L * 1024L * 8L // 8MB
            tileFileSystemThreads = 8
            tileDownloadThreads = 8
        }
        Log.d(TAG, "Configuration terminée: userAgent=${osmConfig.userAgentValue}, cacheSize=${osmConfig.tileFileSystemCacheMaxBytes}")
        
        onDispose {
            Log.d(TAG, "Nettoyage de la configuration")
        }
    }

    var mapView: MapView? by remember { mutableStateOf(null) }
    var selectedLocation: GeoPoint? by remember { mutableStateOf(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    Log.d(TAG, "Permission de localisation: $hasLocationPermission")

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        Log.d(TAG, "Permission de localisation accordée: $isGranted")
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            Log.d(TAG, "Demande de permission de localisation")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val defaultLocation = GeoPoint(46.603354, 1.888334) // Centre de la France
    val initialLocation = if (initialLatitude != null && initialLongitude != null) {
        GeoPoint(initialLatitude, initialLongitude)
    } else {
        defaultLocation
    }
    Log.d(TAG, "Position initiale: lat=${initialLocation.latitude}, lon=${initialLocation.longitude}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélectionner un emplacement") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = { ctx ->
                        Log.d(TAG, "Création de la vue de carte")
                        MapView(ctx).apply {
                            Log.d(TAG, "Configuration de la carte")
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(15.0)
                            controller.setCenter(initialLocation)
                            mapView = this
                            Log.d(TAG, "Carte initialisée avec zoom=15.0 et centre=$initialLocation")

                            // Configuration des contrôles
                            zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                            setBuiltInZoomControls(true)
                            setMultiTouchControls(true)
                            Log.d(TAG, "Contrôles de zoom configurés")

                            // Ajout des gestes de rotation
                            val rotationGestureOverlay = RotationGestureOverlay(this)
                            rotationGestureOverlay.setEnabled(true)
                            overlays.add(rotationGestureOverlay)
                            Log.d(TAG, "Geste de rotation ajouté")

                            // Ajout du marqueur de position sélectionnée
                            setOnClickListener { event ->
                                val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                                Log.d(TAG, "Clic sur la carte: lat=${geoPoint.latitude}, lon=${geoPoint.longitude}")
                                selectedLocation = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                
                                // Mise à jour du marqueur
                                overlays.clear()
                                val marker = Marker(this).apply {
                                    position = selectedLocation
                                    title = "Position sélectionnée"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                }
                                overlays.add(marker)
                                Log.d(TAG, "Marqueur ajouté à la position sélectionnée")
                                
                                // Réajout des overlays de base
                                val compassOverlay = CompassOverlay(
                                    ctx,
                                    InternalCompassOrientationProvider(ctx),
                                    this
                                )
                                compassOverlay.enableCompass()
                                overlays.add(compassOverlay)
                                Log.d(TAG, "Boussole réajoutée")

                                if (hasLocationPermission) {
                                    val locationOverlay = MyLocationNewOverlay(
                                        GpsMyLocationProvider(ctx),
                                        this
                                    )
                                    locationOverlay.enableMyLocation()
                                    overlays.add(locationOverlay)
                                    Log.d(TAG, "Overlay de localisation réajouté")
                                }

                                onLocationSelected(geoPoint.latitude, geoPoint.longitude)
                            }

                            // Ajout de la boussole
                            val compassOverlay = CompassOverlay(
                                ctx,
                                InternalCompassOrientationProvider(ctx),
                                this
                            )
                            compassOverlay.enableCompass()
                            overlays.add(compassOverlay)
                            Log.d(TAG, "Boussole ajoutée")

                            // Ajout de la position actuelle si la permission est accordée
                            if (hasLocationPermission) {
                                val locationOverlay = MyLocationNewOverlay(
                                    GpsMyLocationProvider(ctx),
                                    this
                                )
                                locationOverlay.enableMyLocation()
                                overlays.add(locationOverlay)
                                Log.d(TAG, "Overlay de localisation ajouté")
                            }

                            // Ajout du marqueur initial si une position est fournie
                            if (initialLatitude != null && initialLongitude != null) {
                                val marker = Marker(this).apply {
                                    position = initialLocation
                                    title = "Position initiale"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                }
                                overlays.add(marker)
                                selectedLocation = initialLocation
                                Log.d(TAG, "Marqueur initial ajouté")
                            }

                            invalidate()
                            Log.d(TAG, "Carte invalidée pour forcer le rafraîchissement")
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { map ->
                        Log.d(TAG, "Mise à jour de la carte")
                        map.invalidate()
                    }
                )

                FloatingActionButton(
                    onClick = {
                        Log.d(TAG, "Clic sur le bouton de localisation")
                        mapView?.controller?.animateTo(initialLocation)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Ma position")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annuler")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        selectedLocation?.let { location ->
                            Log.d(TAG, "Validation de la position: lat=${location.latitude}, lon=${location.longitude}")
                            onLocationSelected(location.latitude, location.longitude)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedLocation != null
                ) {
                    Text("Valider")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Nettoyage de la carte")
            mapView?.onDetach()
        }
    }
} 