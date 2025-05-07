package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.data.local.entity.ParcelleEntity
import kotlinx.coroutines.launch
import com.vitizen.app.presentation.components.OsmMapPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelleForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    parcelleId: Long? = null
) {
    var nom by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var cepage by remember { mutableStateOf("") }
    var anneePlantation by remember { mutableStateOf("") }
    var typeConduite by remember { mutableStateOf("") }
    var largeurInterrang by remember { mutableStateOf("") }
    var hauteurFeuillage by remember { mutableStateOf("") }
    var accessibleMateriel by remember { mutableStateOf(listOf<String>()) }
    var zoneSensible by remember { mutableStateOf(false) }
    var zoneHumide by remember { mutableStateOf(false) }
    var drainage by remember { mutableStateOf(false) }
    var enherbement by remember { mutableStateOf(false) }
    var pente by remember { mutableStateOf("") }
    var typeSol by remember { mutableStateOf("") }
    var inondable by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }
    var surfaceError by remember { mutableStateOf(false) }
    var anneePlantationError by remember { mutableStateOf(false) }
    var largeurInterrangError by remember { mutableStateOf(false) }
    var hauteurFeuillageError by remember { mutableStateOf(false) }
    var penteError by remember { mutableStateOf(false) }
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }

    var surfaceErrorMessage by remember { mutableStateOf("") }
    var anneePlantationErrorMessage by remember { mutableStateOf("") }
    var largeurInterrangErrorMessage by remember { mutableStateOf("") }
    var hauteurFeuillageErrorMessage by remember { mutableStateOf("") }
    var penteErrorMessage by remember { mutableStateOf("") }
    var latitudeErrorMessage by remember { mutableStateOf("") }
    var longitudeErrorMessage by remember { mutableStateOf("") }

    var showMapPicker by remember { mutableStateOf(false) }
    var selectedLatitude by remember { mutableStateOf(46.603354) }
    var selectedLongitude by remember { mutableStateOf(1.888334) }
    var codePostal by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(parcelleId) {
        parcelleId?.let { id ->
            viewModel.getParcelleById(id)?.let { parcelle ->
                nom = parcelle.nom
                surface = parcelle.surface.toString()
                cepage = parcelle.cepage
                anneePlantation = parcelle.anneePlantation.toString()
                typeConduite = parcelle.typeConduite
                largeurInterrang = parcelle.largeurInterrang?.toString() ?: ""
                hauteurFeuillage = parcelle.hauteurFeuillage?.toString() ?: ""
                accessibleMateriel = parcelle.accessibleMateriel
                zoneSensible = parcelle.zoneSensible
                zoneHumide = parcelle.zoneHumide
                drainage = parcelle.drainage
                enherbement = parcelle.enherbement
                pente = parcelle.pente
                typeSol = parcelle.typeSol
                inondable = parcelle.inondable
                latitude = parcelle.latitude?.toString() ?: ""
                longitude = parcelle.longitude?.toString() ?: ""
                selectedLatitude = parcelle.latitude ?: 46.603354
                selectedLongitude = parcelle.longitude ?: 1.888334
            }
        }
    }

    if (showMapPicker) {
        Dialog(
            onDismissRequest = { showMapPicker = false }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    OsmMapPicker(
                        initialLatitude = selectedLatitude,
                        initialLongitude = selectedLongitude,
                        postalCode = codePostal,
                        onLocationSelected = { lat, lon ->
                            selectedLatitude = lat
                            selectedLongitude = lon
                            latitude = lat.toString()
                            longitude = lon.toString()
                            showMapPicker = false
                        },
                        onDismiss = { showMapPicker = false }
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (parcelleId == null) "Ajouter une parcelle" else "Modifier la parcelle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom de la parcelle *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && nom.isBlank(),
                supportingText = {
                    if (showErrors && nom.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = surface,
                onValueChange = { 
                    surface = it
                    surfaceError = false
                    surfaceErrorMessage = ""
                },
                label = { Text("Surface (ha) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = surfaceError,
                supportingText = {
                    if (surfaceError) {
                        Text(surfaceErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cepage,
                onValueChange = { cepage = it },
                label = { Text("Cépage *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && cepage.isBlank(),
                supportingText = {
                    if (showErrors && cepage.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = anneePlantation,
                onValueChange = { 
                    anneePlantation = it
                    anneePlantationError = false
                    anneePlantationErrorMessage = ""
                },
                label = { Text("Année de plantation *") },
                modifier = Modifier.fillMaxWidth(),
                isError = anneePlantationError,
                supportingText = {
                    if (anneePlantationError) {
                        Text(anneePlantationErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeConduite,
                onValueChange = { typeConduite = it },
                label = { Text("Type de conduite *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && typeConduite.isBlank(),
                supportingText = {
                    if (showErrors && typeConduite.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = largeurInterrang,
                onValueChange = { 
                    largeurInterrang = it
                    largeurInterrangError = false
                    largeurInterrangErrorMessage = ""
                },
                label = { Text("Largeur interrang (m)") },
                modifier = Modifier.fillMaxWidth(),
                isError = largeurInterrangError,
                supportingText = {
                    if (largeurInterrangError) {
                        Text(largeurInterrangErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = hauteurFeuillage,
                onValueChange = { 
                    hauteurFeuillage = it
                    hauteurFeuillageError = false
                    hauteurFeuillageErrorMessage = ""
                },
                label = { Text("Hauteur du feuillage (m)") },
                modifier = Modifier.fillMaxWidth(),
                isError = hauteurFeuillageError,
                supportingText = {
                    if (hauteurFeuillageError) {
                        Text(hauteurFeuillageErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pente,
                onValueChange = { 
                    pente = it
                    penteError = false
                    penteErrorMessage = ""
                },
                label = { Text("Pente (%)") },
                modifier = Modifier.fillMaxWidth(),
                isError = penteError,
                supportingText = {
                    if (penteError) {
                        Text(penteErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeSol,
                onValueChange = { typeSol = it },
                label = { Text("Type de sol") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = inondable,
                    onCheckedChange = { inondable = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Inondable", modifier = Modifier.weight(3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = codePostal,
                onValueChange = { codePostal = it },
                label = { Text("Code postal") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { 
                        latitude = it
                        latitudeError = false
                        latitudeErrorMessage = ""
                    },
                    label = { Text("Latitude *") },
                    modifier = Modifier.weight(1f),
                    isError = latitudeError,
                    supportingText = {
                        if (latitudeError) {
                            Text(latitudeErrorMessage)
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showMapPicker = true },
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Sélectionner sur la carte")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { 
                    longitude = it
                    longitudeError = false
                    longitudeErrorMessage = ""
                },
                label = { Text("Longitude *") },
                modifier = Modifier.fillMaxWidth(),
                isError = longitudeError,
                supportingText = {
                    if (longitudeError) {
                        Text(longitudeErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "* Champs obligatoires",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    var hasError = false
                    if (nom.isBlank() || cepage.isBlank() || typeConduite.isBlank()) {
                        showErrors = true
                        hasError = true
                    }

                    // Validation des champs numériques
                    fun validateNumericField(
                        value: String,
                        fieldName: String,
                        isRequired: Boolean = false,
                        minValue: Float? = null,
                        maxValue: Float? = null
                    ): Pair<Boolean, String> {
                        if (value.isBlank()) {
                            return if (isRequired) Pair(true, "Ce champ est obligatoire") else Pair(false, "")
                        }
                        return try {
                            val numericValue = value.toFloat()
                            when {
                                minValue != null && numericValue < minValue -> 
                                    Pair(true, "La valeur doit être supérieure à $minValue")
                                maxValue != null && numericValue > maxValue -> 
                                    Pair(true, "La valeur doit être inférieure à $maxValue")
                                else -> Pair(false, "")
                            }
                        } catch (e: NumberFormatException) {
                            Pair(true, "Veuillez entrer un nombre valide")
                        }
                    }

                    // Validation de chaque champ numérique
                    val surfaceValidation = validateNumericField(surface, "Surface", isRequired = true, minValue = 0f)
                    if (surfaceValidation.first) {
                        surfaceError = true
                        surfaceErrorMessage = surfaceValidation.second
                        hasError = true
                    }

                    val anneePlantationValidation = validateNumericField(anneePlantation, "Année de plantation", isRequired = true, minValue = 1900f, maxValue = 2100f)
                    if (anneePlantationValidation.first) {
                        anneePlantationError = true
                        anneePlantationErrorMessage = anneePlantationValidation.second
                        hasError = true
                    }

                    val largeurInterrangValidation = validateNumericField(largeurInterrang, "Largeur interrang", minValue = 0f)
                    if (largeurInterrangValidation.first) {
                        largeurInterrangError = true
                        largeurInterrangErrorMessage = largeurInterrangValidation.second
                        hasError = true
                    }

                    val hauteurFeuillageValidation = validateNumericField(hauteurFeuillage, "Hauteur du feuillage", minValue = 0f)
                    if (hauteurFeuillageValidation.first) {
                        hauteurFeuillageError = true
                        hauteurFeuillageErrorMessage = hauteurFeuillageValidation.second
                        hasError = true
                    }

                    val penteValidation = validateNumericField(pente, "Pente", minValue = 0f, maxValue = 100f)
                    if (penteValidation.first) {
                        penteError = true
                        penteErrorMessage = penteValidation.second
                        hasError = true
                    }

                    val latitudeValidation = validateNumericField(latitude, "Latitude", minValue = -90f, maxValue = 90f)
                    if (latitudeValidation.first) {
                        latitudeError = true
                        latitudeErrorMessage = latitudeValidation.second
                        hasError = true
                    }

                    val longitudeValidation = validateNumericField(longitude, "Longitude", minValue = -180f, maxValue = 180f)
                    if (longitudeValidation.first) {
                        longitudeError = true
                        longitudeErrorMessage = longitudeValidation.second
                        hasError = true
                    }

                    if (!hasError) {
                        try {
                            val parcelle = ParcelleEntity(
                                id = parcelleId ?: 0,
                                nom = nom,
                                surface = surface.toFloat(),
                                cepage = cepage,
                                anneePlantation = anneePlantation.toInt(),
                                typeConduite = typeConduite,
                                largeurInterrang = largeurInterrang.toFloatOrNull(),
                                hauteurFeuillage = hauteurFeuillage.toFloatOrNull(),
                                accessibleMateriel = accessibleMateriel,
                                zoneSensible = zoneSensible,
                                zoneHumide = zoneHumide,
                                drainage = drainage,
                                enherbement = enherbement,
                                pente = pente,
                                typeSol = typeSol,
                                inondable = inondable,
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull()
                            )

                            scope.launch {
                                if (parcelleId == null) {
                                    viewModel.addParcelle(
                                        nom = parcelle.nom,
                                        surface = parcelle.surface,
                                        cepage = parcelle.cepage,
                                        anneePlantation = parcelle.anneePlantation,
                                        typeConduite = parcelle.typeConduite,
                                        largeurInterrang = parcelle.largeurInterrang,
                                        hauteurFeuillage = parcelle.hauteurFeuillage,
                                        accessibleMateriel = parcelle.accessibleMateriel,
                                        zoneSensible = parcelle.zoneSensible,
                                        zoneHumide = parcelle.zoneHumide,
                                        drainage = parcelle.drainage,
                                        enherbement = parcelle.enherbement,
                                        pente = parcelle.pente,
                                        typeSol = parcelle.typeSol,
                                        inondable = parcelle.inondable,
                                        latitude = parcelle.latitude,
                                        longitude = parcelle.longitude
                                    )
                                } else {
                                    viewModel.updateParcelle(
                                        id = parcelle.id,
                                        nom = parcelle.nom,
                                        surface = parcelle.surface,
                                        cepage = parcelle.cepage,
                                        anneePlantation = parcelle.anneePlantation,
                                        typeConduite = parcelle.typeConduite,
                                        largeurInterrang = parcelle.largeurInterrang,
                                        hauteurFeuillage = parcelle.hauteurFeuillage,
                                        accessibleMateriel = parcelle.accessibleMateriel,
                                        zoneSensible = parcelle.zoneSensible,
                                        zoneHumide = parcelle.zoneHumide,
                                        drainage = parcelle.drainage,
                                        enherbement = parcelle.enherbement,
                                        pente = parcelle.pente,
                                        typeSol = parcelle.typeSol,
                                        inondable = parcelle.inondable,
                                        latitude = parcelle.latitude,
                                        longitude = parcelle.longitude
                                    )
                                }
                                onNavigateBack()
                            }
                        } catch (e: NumberFormatException) {
                            // Gestion des erreurs de conversion
                            showErrors = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (parcelleId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 