package com.vitizen.app.presentation.screen.home.section.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.CustomZoomButtonsController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.domain.model.Parcelle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import androidx.core.content.ContextCompat
import com.vitizen.app.R


/**
 * Composant principal pour l'affichage et la gestion des parcelles
 */
@Composable
fun ParcellesBox(
    onNavigateToForm: (String) -> Unit
) {
    val viewModel: ParcellesViewModel = hiltViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)
    
    // Observation des états du ViewModel
    val parcelles by viewModel.parcelles.collectAsState()
    val selectedParcelle by viewModel.selectedParcelle.collectAsState()
    val isPointMode by viewModel.isPointMode.collectAsState()
    val isPolygonMode by viewModel.isPolygonMode.collectAsState()
    val markerPosition by viewModel.markerPosition.collectAsState()
    val shouldClearMarkers by viewModel.shouldClearMarkers.collectAsState()
    
    // Effet pour gérer les marqueurs en suivant les instructions du ViewModel
    LaunchedEffect(markerPosition, shouldClearMarkers) {
        try {
            // Nettoyer les marqueurs si demandé
            if (shouldClearMarkers) {
                mapView.overlays
                    .filterIsInstance<Marker>()
                    .filter { it.id == "temp_marker" }
                    .forEach { mapView.overlays.remove(it) }
                
                // Informer le ViewModel que les marqueurs ont été nettoyés
                viewModel.markersCleared()
            }
            
            // Ajouter le marqueur si une position est définie
            markerPosition?.let { position ->
                val marker = Marker(mapView)
                marker.id = "temp_marker"
                marker.position = position
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                
                // Désactiver l'infoview lorsqu'on clique sur le marqueur
                marker.setOnMarkerClickListener { _, _ -> 
                    true // Retourne true pour indiquer que l'événement a été géré
                }
                
                // Désactiver le texte et l'infowindow
                marker.title = null
                marker.snippet = null
                marker.setInfoWindow(null)
                
                mapView.overlays.add(marker)
            }
            
            // Rafraîchir la carte
            mapView.invalidate()
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors de la gestion des marqueurs: ${e.message}")
        }
    }

    // Effet pour gérer les événements de clic sur la carte selon le mode actif
    LaunchedEffect(isPointMode, isPolygonMode, mapView) {
        try {
            // Supprimer les overlays d'événements précédents
            mapView.overlays
                .filterIsInstance<MapEventsOverlay>()
                .forEach { mapView.overlays.remove(it) }
            
            // Ajouter les gestionnaires d'événements si un mode est actif
            if (isPointMode || isPolygonMode) {
                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { geoPoint ->
                            // Déléguer la gestion du clic au ViewModel
                            if (isPointMode) {
                                viewModel.setMarkerPosition(geoPoint)
                            } else if (isPolygonMode) {
                                // Pour une future implémentation
                            }
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false // Non utilisé
                    }
                }
                
                // Ajouter l'overlay d'événements à la carte
                val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
                mapView.overlays.add(0, mapEventsOverlay)
            }
            
            // Rafraîchir la carte
            mapView.invalidate()
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors de la gestion des événements: ${e.message}")
        }
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
                modifier = Modifier.fillMaxSize()
            )
            
            // Boutons de sélection de mode en haut à gauche
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                // Bouton mode polygone
                IconButton(
                    onClick = { viewModel.togglePolygonMode() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isPolygonMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChangeHistory,
                        contentDescription = "Mode polygone",
                        tint = if (isPolygonMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bouton mode point unique
                IconButton(
                    onClick = { viewModel.togglePointMode() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isPointMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Mode point unique",
                        tint = if (isPointMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Bouton d'ajout en bas à droite qui apparaît uniquement lorsqu'un marqueur est présent
            if (markerPosition != null) {
                IconButton(
                    onClick = { 
                        // Créer une parcelle à partir du point marqué
                        markerPosition?.let { position ->
                            onNavigateToForm("parcelle_form/new?lat=${position.latitude}&lon=${position.longitude}")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter une parcelle à cette position",
                        tint = MaterialTheme.colorScheme.primary
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
                .padding(16.dp)
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = parcelles,
                        key = { parcelle -> parcelle.id }
                    ) { parcelle ->
                        ParcelleItem(
                            parcelle = parcelle,
                            onEdit = { onNavigateToForm("parcelle_form/${parcelle.id}") },
                            onDelete = { viewModel.setParcelleToDelete(parcelle) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParcelleItem(
    parcelle: Parcelle,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                    text = "${parcelle.surface} ha - ${parcelle.cepage}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier la parcelle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer la parcelle",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Composable pour gérer le cycle de vie de la MapView
 */
@Composable
private fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            Configuration.getInstance().userAgentValue = context.packageName
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(47.0242, 4.8386))
            
            // Désactiver les boutons de zoom
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
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