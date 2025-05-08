package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.vitizen.app.domain.model.Parcelle
import com.vitizen.app.presentation.components.OsmMapPicker
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.util.*
import org.osmdroid.views.overlay.Polygon
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcellesView(
    parcelles: List<Parcelle>,
    onParcelleAdded: (Parcelle) -> Unit,
    onParcelleDeleted: (Parcelle) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isSatelliteView by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showParcelleForm by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var showDefineButton by remember { mutableStateOf(false) }

    // États des champs du formulaire
    var name by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var cepage by remember { mutableStateOf("") }
    var typeConduite by remember { mutableStateOf("") }
    var largeur by remember { mutableStateOf("") }
    var hauteur by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        // Carte
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(46.603354, 1.888334))
                    
                    // Ajouter les marqueurs pour les parcelles existantes
                    parcelles.forEach { parcelle ->
                        if (parcelle.polygonPoints.isEmpty()) {
                            val marker = Marker(this).apply {
                                position = GeoPoint(parcelle.latitude, parcelle.longitude)
                                title = parcelle.name
                                snippet = "${parcelle.surface} ha - ${parcelle.cepage}"
                            }
                            overlays.add(marker)
                        } else {
                            // Ajouter le polygone
                            val polygon = Polygon().apply {
                                points = parcelle.polygonPoints
                                fillColor = AndroidColor.argb(60, 0, 0, 255)
                                strokeColor = AndroidColor.BLUE
                                strokeWidth = 4f
                            }
                            overlays.add(polygon)
                        }
                    }

                    // Gestion des clics sur la carte
                    overlays.add(object : Overlay() {
                        override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                            e?.let {
                                val projection = mapView?.projection
                                val point = projection?.fromPixels(it.x.toInt(), it.y.toInt())
                                point?.let { geoPoint ->
                                    selectedLocation = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                    showDefineButton = true
                                }
                            }
                            return true
                        }
                    })
                    
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.setTileSource(if (isSatelliteView) TileSourceFactory.USGS_SAT else TileSourceFactory.MAPNIK)
            }
        )

        // Bouton de changement de vue
        IconButton(
            onClick = { isSatelliteView = !isSatelliteView },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Icon(
                imageVector = if (isSatelliteView) Icons.Default.Map else Icons.Default.Satellite,
                contentDescription = if (isSatelliteView) "Vue carte" else "Vue satellite"
            )
        }

        // Bouton Définir
        if (showDefineButton) {
            Button(
                onClick = {
                    showParcelleForm = true
                    showDefineButton = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Définir")
            }
        }
    }

    // Dialog d'ajout de parcelle
    if (showParcelleForm) {
        AlertDialog(
            onDismissRequest = { 
                showParcelleForm = false
                selectedLocation = null
                // Réinitialiser les champs
                name = ""
                surface = ""
                cepage = ""
                typeConduite = ""
                largeur = ""
                hauteur = ""
            },
            title = { Text("Nouvelle parcelle") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = surface,
                        onValueChange = { surface = it },
                        label = { Text("Surface (ha)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cepage,
                        onValueChange = { cepage = it },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = typeConduite,
                        onValueChange = { typeConduite = it },
                        label = { Text("Type de conduite") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = largeur,
                            onValueChange = { largeur = it },
                            label = { Text("Largeur") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hauteur,
                            onValueChange = { hauteur = it },
                            label = { Text("Hauteur") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedLocation?.let { location ->
                            onParcelleAdded(
                                Parcelle(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    surface = surface.toDoubleOrNull() ?: 0.0,
                                    cepage = cepage,
                                    typeConduite = typeConduite,
                                    largeur = largeur.toDoubleOrNull() ?: 0.0,
                                    hauteur = hauteur.toDoubleOrNull() ?: 0.0,
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        }
                        showParcelleForm = false
                        selectedLocation = null
                        // Réinitialiser les champs
                        name = ""
                        surface = ""
                        cepage = ""
                        typeConduite = ""
                        largeur = ""
                        hauteur = ""
                    }
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showParcelleForm = false
                        selectedLocation = null
                        // Réinitialiser les champs
                        name = ""
                        surface = ""
                        cepage = ""
                        typeConduite = ""
                        largeur = ""
                        hauteur = ""
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
} 