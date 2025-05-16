package com.vitizen.app.presentation.screen.home.section.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import java.util.UUID
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import androidx.compose.ui.graphics.Color

// Constante pour identifier le marqueur temporaire
private const val TEMPORARY_MARKER_ID = "temp_marker"

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
    
    // État pour le formulaire d'ajout
    var showAddParcelleDialog by remember { mutableStateOf(false) }
    var parcelleName by remember { mutableStateOf("") }
    var parcelleSurface by remember { mutableStateOf("") }
    var parcelleCepage by remember { mutableStateOf("") }
    var parcelleTypeConduite by remember { mutableStateOf("") }
    var parcelleLargeur by remember { mutableStateOf("") }
    var parcelleHauteur by remember { mutableStateOf("") }
    
    // État pour le formulaire d'édition
    var showEditParcelleDialog by remember { mutableStateOf(false) }
    var parcelleToEdit by remember { mutableStateOf<Parcelle?>(null) }
    
    // État pour la confirmation de suppression
    var parcelleToDelete by remember { mutableStateOf<Parcelle?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // Validation du formulaire
    val isFormValid = parcelleName.isNotBlank() && 
                      parcelleSurface.isNotBlank() && 
                      parcelleCepage.isNotBlank() &&
                      parcelleTypeConduite.isNotBlank() &&
                      parcelleLargeur.isNotBlank() &&
                      parcelleHauteur.isNotBlank()

    // Fonction pour réinitialiser le formulaire
    fun resetForm() {
        parcelleName = ""
        parcelleSurface = ""
        parcelleCepage = ""
        parcelleTypeConduite = ""
        parcelleLargeur = ""
        parcelleHauteur = ""
        showAddParcelleDialog = false
        showEditParcelleDialog = false
        parcelleToEdit = null
    }
    
    // Fonction pour préparer le formulaire d'édition
    fun prepareEditForm(parcelle: Parcelle) {
        parcelleName = parcelle.name
        parcelleSurface = parcelle.surface.toString()
        parcelleCepage = parcelle.cepage
        parcelleTypeConduite = parcelle.typeConduite
        parcelleLargeur = parcelle.largeur.toString()
        parcelleHauteur = parcelle.hauteur.toString()
        parcelleToEdit = parcelle
        showEditParcelleDialog = true
    }
    
    // Fonction pour fermer toutes les infowindows ouvertes
    fun closeAllInfoWindows() {
        try {
            // Parcourir tous les marqueurs et fermer leurs infowindows
            mapView.overlays
                .filterIsInstance<Marker>()
                .forEach { marker ->
                    marker.closeInfoWindow()
                }
            mapView.invalidate()
            Log.d("ParcellesBox", "Toutes les infowindows ont été fermées")
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors de la fermeture des infowindows", e)
        }
    }
    
    // Observation des états du ViewModel
    val parcelles by viewModel.parcelles.collectAsState()
    val selectedParcelle by viewModel.selectedParcelle.collectAsState()
    val isPointMode by viewModel.isPointMode.collectAsState()
    val isPolygonMode by viewModel.isPolygonMode.collectAsState()
    val markerPosition by viewModel.markerPosition.collectAsState()
    val shouldClearMarkers by viewModel.shouldClearMarkers.collectAsState()
    val permanentMarkers by viewModel.permanentMarkers.collectAsState()
    
    // États pour le mode polygon
    val polygonPoints by viewModel.polygonPoints.collectAsState()
    val isDrawingPolygon by viewModel.isDrawingPolygon.collectAsState()
    var polygonOverlay by remember { mutableStateOf<Polygon?>(null) }
    var tempMarker by remember { mutableStateOf<Marker?>(null) }
    
    // Fonction pour sélectionner une parcelle au clic sur la card
    fun selectParcelleAndCenterMap(parcelle: Parcelle) {
        viewModel.selectParcelle(parcelle)
        // Fermer toutes les infowindows
        closeAllInfoWindows()
        // Créer un point à partir des coordonnées de la parcelle
        val position = GeoPoint(parcelle.latitude, parcelle.longitude)
        
        try {
            // Animer la transition vers le point avec un zoom approprié
            mapView.controller.animateTo(position, 15.0, 1000L)
            Log.d("ParcellesBox", "Centrage animé sur la parcelle: ${parcelle.name}")
        } catch (e: Exception) {
            // Fallback en cas d'erreur avec l'animation
            Log.e("ParcellesBox", "Erreur lors de l'animation de la carte", e)
            mapView.controller.setCenter(position)
            mapView.controller.setZoom(15.0)
        }
    }
    
    // Fonction pour centrer la carte sur toutes les parcelles
    fun centerOnAllParcelles() {
        // Fermer toutes les infowindows
        closeAllInfoWindows()
        
        val parcellesList = parcelles // Capture locale de la variable parcelles
        
        if (parcellesList.isEmpty()) {
            // Si aucune parcelle, centrer sur Beaune
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(47.0242, 4.8386))
            return
        }
        
        try {
            // Cas spécial: s'il n'y a qu'une seule parcelle
            if (parcellesList.size == 1) {
                val parcelle = parcellesList[0]
                val position = GeoPoint(parcelle.latitude, parcelle.longitude)
                
                // Animer la transition vers le point avec un zoom approprié
                mapView.controller.animateTo(position, 15.0, 1000L)
                Log.d("ParcellesBox", "Carte centrée avec animation sur la parcelle unique: ${parcelle.name}")
                return
            }
            
            // Pour plusieurs parcelles, calculer les limites (bounds)
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            
            parcellesList.forEach { parcelle ->
                minLat = minOf(minLat, parcelle.latitude)
                maxLat = maxOf(maxLat, parcelle.latitude)
                minLon = minOf(minLon, parcelle.longitude)
                maxLon = maxOf(maxLon, parcelle.longitude)
            }
            
            // Ajouter une marge (seulement pour plusieurs parcelles)
            val latSpan = maxLat - minLat
            val lonSpan = maxLon - minLon
            val margin = 0.2 // 20% de marge
            
            minLat -= latSpan * margin
            maxLat += latSpan * margin
            minLon -= lonSpan * margin
            maxLon += lonSpan * margin
            
            // Créer un rectangle de délimitation
            val boundingBox = org.osmdroid.util.BoundingBox(
                maxLat, maxLon, minLat, minLon
            )
            
            // Animer la carte pour afficher toutes les parcelles
            mapView.zoomToBoundingBox(boundingBox, true)
            
            Log.d("ParcellesBox", "Carte centrée sur ${parcellesList.size} parcelles: $boundingBox")
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors du centrage sur toutes les parcelles", e)
            // En cas d'erreur, centrer sur Beaune
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(47.0242, 4.8386))
        }
    }
    
    // Effet pour gérer le marqueur temporaire
    LaunchedEffect(markerPosition, shouldClearMarkers) {
        try {
            // Nettoyer les marqueurs temporaires si demandé
            if (shouldClearMarkers) {
                mapView.overlays
                    .filterIsInstance<Marker>()
                    .filter { it.id == TEMPORARY_MARKER_ID }
                    .forEach { mapView.overlays.remove(it) }
                
                // Informer le ViewModel que les marqueurs ont été nettoyés
                viewModel.markersCleared()
            }
            
            // Ajouter le marqueur temporaire si une position est définie
            markerPosition?.let { position ->
                // Supprimer tout marqueur temporaire existant pour éviter les doublons
                mapView.overlays
                    .filterIsInstance<Marker>()
                    .filter { it.id == TEMPORARY_MARKER_ID }
                    .forEach { mapView.overlays.remove(it) }
                
                // Créer un nouveau marqueur temporaire avec l'icône ROUGE
                val icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)
                // S'assurer que l'icône ne reçoit aucune teinte
                icon?.clearColorFilter()
                
                val marker = Marker(mapView).apply {
                    id = TEMPORARY_MARKER_ID
                    this.position = position
                    this.icon = icon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    
                    // Désactiver l'infoview
                    title = null
                    snippet = null
                    setInfoWindow(null)
                    
                    // Désactiver le comportement par défaut du clic
                    setOnMarkerClickListener { _, _ -> true }
                }
                
                Log.d("ParcellesBox", "Ajout d'un marqueur temporaire ROUGE à la position: ${position.latitude}, ${position.longitude}")
                mapView.overlays.add(marker)
                mapView.invalidate()
            }
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors de la gestion du marqueur temporaire: ${e.message}")
        }
    }
    
    // Effet pour gérer les marqueurs permanents
    LaunchedEffect(permanentMarkers) {
        try {
            // Supprimer tous les marqueurs permanents existants
            val markersToRemove = mapView.overlays
                .filterIsInstance<Marker>()
                .filter { it.id != null && it.id != TEMPORARY_MARKER_ID }
                
            markersToRemove.forEach { marker ->
                mapView.overlays.remove(marker)
            }
            
            // Ajouter les marqueurs permanents pour chaque parcelle avec une teinte GRISE
            permanentMarkers.forEach { (parcelleId, position) ->
                // Créer un drawable pour chaque marqueur pour éviter le partage de référence
                val grayIcon = ContextCompat.getDrawable(context, R.drawable.ic_marker)?.mutate()
                grayIcon?.setTint(android.graphics.Color.GRAY)
                
                // Trouver la parcelle correspondante
                val parcelle = parcelles.find { it.id == parcelleId }
                
                val marker = Marker(mapView).apply {
                    id = "permanent_$parcelleId"
                    this.position = position
                    this.icon = grayIcon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    
                    // Configurer l'infowindow avec les informations de la parcelle
                    parcelle?.let {
                        title = it.name
                        snippet = "${it.surface} ha - ${it.cepage}"
                        
                        // Permettre d'afficher l'infowindow mais sélectionner quand même la parcelle
                        setOnMarkerClickListener { marker, _ ->
                            // Afficher l'infowindow
                            marker.showInfoWindow()
                            
                            // Sélectionner la parcelle
                            viewModel.selectParcelle(it)
                            
                            // Retourner true pour indiquer que l'événement a été géré
                            true
                        }
                    }
                }
                
                Log.d("ParcellesBox", "Ajout d'un marqueur permanent GRIS pour la parcelle: $parcelleId")
                mapView.overlays.add(marker)
            }
            
            mapView.invalidate()
        } catch (e: Exception) {
            Log.e("ParcellesBox", "Erreur lors de la gestion des marqueurs permanents: ${e.message}")
        }
    }

    // Effet pour gérer les événements de clic sur la carte selon le mode actif
    LaunchedEffect(isPointMode, isPolygonMode, mapView) {
        try {
            // Supprimer les overlays d'événements précédents
            val eventsToRemove = mapView.overlays
                .filterIsInstance<MapEventsOverlay>()
            
            eventsToRemove.forEach { overlay ->
                mapView.overlays.remove(overlay)
            }
            
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
                
                // Ajouter l'overlay d'événements à la carte (index 0 pour priorité)
                val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
                mapView.overlays.add(0, mapEventsOverlay)
            } else {
                // Si aucun mode d'édition n'est actif, ajouter un gestionnaire pour le double-clic
                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        return false // Laisser passer les évènements de clic simple
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        // Utiliser le longPress comme un double-clic pour afficher toutes les parcelles
                        Log.d("ParcellesBox", "Double-clic détecté, centrage sur toutes les parcelles")
                        centerOnAllParcelles()
                        return true
                    }
                }
                
                val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
                mapView.overlays.add(0, mapEventsOverlay)
            }
            
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
                    onClick = { 
                        viewModel.togglePolygonMode()
                        closeAllInfoWindows()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isPolygonMode) MaterialTheme.colorScheme.primaryContainer 
                                   else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .border(
                            width = 1.dp,
                            color = if (isPolygonMode) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChangeHistory,
                        contentDescription = "Mode polygone",
                        tint = if (isPolygonMode) MaterialTheme.colorScheme.onPrimaryContainer 
                               else MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bouton mode point unique
                IconButton(
                    onClick = { 
                        viewModel.togglePointMode()
                        closeAllInfoWindows()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isPointMode) MaterialTheme.colorScheme.primaryContainer 
                                   else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .border(
                            width = 1.dp,
                            color = if (isPointMode) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Mode point unique",
                        tint = if (isPointMode) MaterialTheme.colorScheme.onPrimaryContainer 
                               else MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Bouton d'ajout en bas à gauche qui apparaît uniquement lorsqu'un marqueur est présent
            if (markerPosition != null) {
                IconButton(
                    onClick = { 
                        showAddParcelleDialog = true
                        closeAllInfoWindows()
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
            // Afficher la liste des parcelles ou un message approprié selon l'état
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
                            onCardClick = { selectParcelleAndCenterMap(parcelle) },
                            onEdit = { prepareEditForm(parcelle) },
                            onDelete = { 
                                parcelleToDelete = parcelle
                                showDeleteConfirmDialog = true 
                            }
                        )
                    }
                }
            }
            
            // Superposition qui bloque les interactions quand un mode d'édition est actif
            if (isPointMode || isPolygonMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .clickable(enabled = false) { /* Bloquer les clics */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isPointMode) "Mode point unique actif" else "Mode polygon actif",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Liste de parcelles désactivée",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // AlertDialog de confirmation de suppression
    if (showDeleteConfirmDialog && parcelleToDelete != null) {
        // Fermer toutes les infowindows avant d'afficher la boîte de dialogue
        closeAllInfoWindows()
        
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmDialog = false
                parcelleToDelete = null
            },
            title = { Text("Confirmation de suppression") },
            text = { 
                Text(
                    "Êtes-vous sûr de vouloir supprimer la parcelle \"${parcelleToDelete?.name}\" ?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = { 
                        parcelleToDelete?.let { parcelle ->
                            viewModel.setParcelleToDelete(parcelle)
                            showDeleteConfirmDialog = false
                            parcelleToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteConfirmDialog = false
                        parcelleToDelete = null
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
    
    // Dialog d'édition de parcelle
    if (showEditParcelleDialog && parcelleToEdit != null) {
        // Fermer toutes les infowindows avant d'afficher la boîte de dialogue
        closeAllInfoWindows()
        
        AlertDialog(
            onDismissRequest = { resetForm() },
            title = { Text("Modifier la parcelle") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = parcelleName,
                        onValueChange = { parcelleName = it },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleSurface,
                        onValueChange = { parcelleSurface = it },
                        label = { Text("Surface (ha)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleCepage,
                        onValueChange = { parcelleCepage = it },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleTypeConduite,
                        onValueChange = { parcelleTypeConduite = it },
                        label = { Text("Type de conduite") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = parcelleLargeur,
                            onValueChange = { parcelleLargeur = it },
                            label = { Text("Largeur") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = parcelleHauteur,
                            onValueChange = { parcelleHauteur = it },
                            label = { Text("Hauteur") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        parcelleToEdit?.let { oldParcelle ->
                            // Créer une parcelle mise à jour avec les nouvelles données du formulaire
                            val updatedParcelle = oldParcelle.copy(
                                name = parcelleName,
                                surface = parcelleSurface.toDoubleOrNull() ?: oldParcelle.surface,
                                cepage = parcelleCepage,
                                typeConduite = parcelleTypeConduite,
                                largeur = parcelleLargeur.toDoubleOrNull() ?: oldParcelle.largeur,
                                hauteur = parcelleHauteur.toDoubleOrNull() ?: oldParcelle.hauteur
                            )
                            
                            // Mettre à jour la parcelle via le ViewModel
                            viewModel.updateParcelle(updatedParcelle)
                            
                            // Réinitialiser le formulaire
                            resetForm()
                        }
                    },
                    enabled = isFormValid && parcelleToEdit != null
                ) {
                    Text("Mettre à jour")
                }
            },
            dismissButton = {
                TextButton(onClick = { resetForm() }) {
                    Text("Annuler")
                }
            }
        )
    }
    
    // Dialog d'ajout de parcelle
    if (showAddParcelleDialog) {
        AlertDialog(
            onDismissRequest = { resetForm() },
            title = { Text("Nouvelle parcelle") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = parcelleName,
                        onValueChange = { parcelleName = it },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleSurface,
                        onValueChange = { parcelleSurface = it },
                        label = { Text("Surface (ha)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleCepage,
                        onValueChange = { parcelleCepage = it },
                        label = { Text("Cépage") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = parcelleTypeConduite,
                        onValueChange = { parcelleTypeConduite = it },
                        label = { Text("Type de conduite") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = parcelleLargeur,
                            onValueChange = { parcelleLargeur = it },
                            label = { Text("Largeur") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = parcelleHauteur,
                            onValueChange = { parcelleHauteur = it },
                            label = { Text("Hauteur") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        markerPosition?.let { position ->
                            // Créer une nouvelle parcelle avec les données du formulaire
                            val newParcelle = Parcelle(
                                id = UUID.randomUUID().toString(),
                                name = parcelleName,
                                surface = parcelleSurface.toDoubleOrNull() ?: 0.0,
                                cepage = parcelleCepage,
                                typeConduite = parcelleTypeConduite,
                                largeur = parcelleLargeur.toDoubleOrNull() ?: 0.0,
                                hauteur = parcelleHauteur.toDoubleOrNull() ?: 0.0,
                                latitude = position.latitude,
                                longitude = position.longitude,
                                polygonPoints = emptyList()  // Point unique, pas de polygone
                            )
                            
                            // Ajouter la parcelle via le ViewModel et terminer le mode point
                            viewModel.addParcelle(newParcelle)
                            
                            // Réinitialiser le formulaire
                            resetForm()
                        }
                    },
                    enabled = isFormValid && markerPosition != null
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { resetForm() }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParcelleItem(
    parcelle: Parcelle,
    onCardClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)  // Hauteur fixe réduite
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Partie texte en une seule ligne
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parcelle.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${parcelle.surface} ha - ${parcelle.cepage}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Boutons d'action
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier la parcelle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
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