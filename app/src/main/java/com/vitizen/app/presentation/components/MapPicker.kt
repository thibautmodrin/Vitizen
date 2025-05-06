package com.vitizen.app.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapPicker(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    postalCode: String,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        Log.d("MapPicker", "Permission de localisation accordée : $isGranted")
    }

    // Position initiale de la caméra
    val defaultLocation = LatLng(46.227638, 2.213749) // Centre de la France
    val initialLocation = if (initialLatitude != null && initialLongitude != null) {
        LatLng(initialLatitude, initialLongitude)
    } else {
        defaultLocation
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    // Demande de permission au démarrage
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            Log.d("MapPicker", "Demande de permission de localisation")
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // En-tête avec le code postal
            Text(
                text = "Sélectionnez l'emplacement de la parcelle",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Code postal : $postalCode",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Carte
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        scope.launch {
                            Log.d("MapPicker", "Clic sur la carte : ${latLng.latitude}, ${latLng.longitude}")
                            selectedLocation = latLng
                        }
                    },
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission,
                        mapType = MapType.NORMAL,
                        isTrafficEnabled = false,
                        isIndoorEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = hasLocationPermission,
                        mapToolbarEnabled = false,
                        compassEnabled = false,
                        rotationGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        scrollGesturesEnabled = true,
                        zoomGesturesEnabled = true
                    )
                ) {
                    // Marqueur pour la position sélectionnée
                    selectedLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Position sélectionnée"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            scope.launch {
                                Log.d("MapPicker", "Validation de la position : ${location.latitude}, ${location.longitude}")
                                onLocationSelected(location.latitude, location.longitude)
                                onDismiss()
                            }
                        }
                    },
                    enabled = selectedLocation != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Valider")
                }
            }
        }
    }
} 