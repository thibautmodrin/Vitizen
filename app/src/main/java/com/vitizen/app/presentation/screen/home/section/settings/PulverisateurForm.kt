package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.data.local.entity.PulverisateurEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulverisateurForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    pulverisateurId: Long? = null
) {
    var nomMateriel by remember { mutableStateOf("") }
    var modeDeplacement by remember { mutableStateOf("") }
    var nombreRampes by remember { mutableStateOf("") }
    var nombreBusesParRampe by remember { mutableStateOf("") }
    var typeBuses by remember { mutableStateOf("") }
    var pressionPulverisation by remember { mutableStateOf("") }
    var debitParBuse by remember { mutableStateOf("") }
    var anglePulverisation by remember { mutableStateOf("") }
    var largeurTraitement by remember { mutableStateOf("") }
    var plageVitesseAvancementMin by remember { mutableStateOf("") }
    var plageVitesseAvancementMax by remember { mutableStateOf("") }
    var volumeTotalCuve by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }
    var nombreRampesError by remember { mutableStateOf(false) }
    var nombreBusesParRampeError by remember { mutableStateOf(false) }
    var pressionPulverisationError by remember { mutableStateOf(false) }
    var debitParBuseError by remember { mutableStateOf(false) }
    var anglePulverisationError by remember { mutableStateOf(false) }
    var largeurTraitementError by remember { mutableStateOf(false) }
    var plageVitesseAvancementMinError by remember { mutableStateOf(false) }
    var plageVitesseAvancementMaxError by remember { mutableStateOf(false) }
    var volumeTotalCuveError by remember { mutableStateOf(false) }

    var nombreRampesErrorMessage by remember { mutableStateOf("") }
    var nombreBusesParRampeErrorMessage by remember { mutableStateOf("") }
    var pressionPulverisationErrorMessage by remember { mutableStateOf("") }
    var debitParBuseErrorMessage by remember { mutableStateOf("") }
    var anglePulverisationErrorMessage by remember { mutableStateOf("") }
    var largeurTraitementErrorMessage by remember { mutableStateOf("") }
    var plageVitesseAvancementMinErrorMessage by remember { mutableStateOf("") }
    var plageVitesseAvancementMaxErrorMessage by remember { mutableStateOf("") }
    var volumeTotalCuveErrorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(pulverisateurId) {
        pulverisateurId?.let { id ->
            viewModel.getPulverisateurById(id)?.let { pulverisateur ->
                nomMateriel = pulverisateur.nomMateriel
                modeDeplacement = pulverisateur.modeDeplacement
                nombreRampes = pulverisateur.nombreRampes?.toString() ?: ""
                nombreBusesParRampe = pulverisateur.nombreBusesParRampe?.toString() ?: ""
                typeBuses = pulverisateur.typeBuses
                pressionPulverisation = pulverisateur.pressionPulverisation?.toString() ?: ""
                debitParBuse = pulverisateur.debitParBuse?.toString() ?: ""
                anglePulverisation = pulverisateur.anglePulverisation?.toString() ?: ""
                largeurTraitement = pulverisateur.largeurTraitement?.toString() ?: ""
                plageVitesseAvancementMin = pulverisateur.plageVitesseAvancementMin?.toString() ?: ""
                plageVitesseAvancementMax = pulverisateur.plageVitesseAvancementMax?.toString() ?: ""
                volumeTotalCuve = pulverisateur.volumeTotalCuve?.toString() ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (pulverisateurId == null) "Ajouter un pulvérisateur" else "Modifier le pulvérisateur") },
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
                value = nomMateriel,
                onValueChange = { nomMateriel = it },
                label = { Text("Nom du matériel *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && nomMateriel.isBlank(),
                supportingText = {
                    if (showErrors && nomMateriel.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = modeDeplacement,
                onValueChange = { modeDeplacement = it },
                label = { Text("Mode de déplacement *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && modeDeplacement.isBlank(),
                supportingText = {
                    if (showErrors && modeDeplacement.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreRampes,
                onValueChange = { 
                    nombreRampes = it
                    nombreRampesError = false
                    nombreRampesErrorMessage = ""
                },
                label = { Text("Nombre de rampes") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreRampesError,
                supportingText = {
                    if (nombreRampesError) {
                        Text(nombreRampesErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreBusesParRampe,
                onValueChange = { 
                    nombreBusesParRampe = it
                    nombreBusesParRampeError = false
                    nombreBusesParRampeErrorMessage = ""
                },
                label = { Text("Nombre de buses par rampe") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreBusesParRampeError,
                supportingText = {
                    if (nombreBusesParRampeError) {
                        Text(nombreBusesParRampeErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeBuses,
                onValueChange = { typeBuses = it },
                label = { Text("Type de buses *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && typeBuses.isBlank(),
                supportingText = {
                    if (showErrors && typeBuses.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pressionPulverisation,
                onValueChange = { 
                    pressionPulverisation = it
                    pressionPulverisationError = false
                    pressionPulverisationErrorMessage = ""
                },
                label = { Text("Pression de pulvérisation (bar)") },
                modifier = Modifier.fillMaxWidth(),
                isError = pressionPulverisationError,
                supportingText = {
                    if (pressionPulverisationError) {
                        Text(pressionPulverisationErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = debitParBuse,
                onValueChange = { 
                    debitParBuse = it
                    debitParBuseError = false
                    debitParBuseErrorMessage = ""
                },
                label = { Text("Débit par buse (l/min)") },
                modifier = Modifier.fillMaxWidth(),
                isError = debitParBuseError,
                supportingText = {
                    if (debitParBuseError) {
                        Text(debitParBuseErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = anglePulverisation,
                onValueChange = { 
                    anglePulverisation = it
                    anglePulverisationError = false
                    anglePulverisationErrorMessage = ""
                },
                label = { Text("Angle de pulvérisation (°)") },
                modifier = Modifier.fillMaxWidth(),
                isError = anglePulverisationError,
                supportingText = {
                    if (anglePulverisationError) {
                        Text(anglePulverisationErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = largeurTraitement,
                onValueChange = { 
                    largeurTraitement = it
                    largeurTraitementError = false
                    largeurTraitementErrorMessage = ""
                },
                label = { Text("Largeur de traitement (m)") },
                modifier = Modifier.fillMaxWidth(),
                isError = largeurTraitementError,
                supportingText = {
                    if (largeurTraitementError) {
                        Text(largeurTraitementErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plageVitesseAvancementMin,
                onValueChange = { 
                    plageVitesseAvancementMin = it
                    plageVitesseAvancementMinError = false
                    plageVitesseAvancementMinErrorMessage = ""
                },
                label = { Text("Vitesse d'avancement min (km/h)") },
                modifier = Modifier.fillMaxWidth(),
                isError = plageVitesseAvancementMinError,
                supportingText = {
                    if (plageVitesseAvancementMinError) {
                        Text(plageVitesseAvancementMinErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plageVitesseAvancementMax,
                onValueChange = { 
                    plageVitesseAvancementMax = it
                    plageVitesseAvancementMaxError = false
                    plageVitesseAvancementMaxErrorMessage = ""
                },
                label = { Text("Vitesse d'avancement max (km/h)") },
                modifier = Modifier.fillMaxWidth(),
                isError = plageVitesseAvancementMaxError,
                supportingText = {
                    if (plageVitesseAvancementMaxError) {
                        Text(plageVitesseAvancementMaxErrorMessage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = volumeTotalCuve,
                onValueChange = { 
                    volumeTotalCuve = it
                    volumeTotalCuveError = false
                    volumeTotalCuveErrorMessage = ""
                },
                label = { Text("Volume total de la cuve (l)") },
                modifier = Modifier.fillMaxWidth(),
                isError = volumeTotalCuveError,
                supportingText = {
                    if (volumeTotalCuveError) {
                        Text(volumeTotalCuveErrorMessage)
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
                    if (nomMateriel.isBlank() || modeDeplacement.isBlank() || typeBuses.isBlank()) {
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
                    val nombreRampesValidation = validateNumericField(nombreRampes, "Nombre de rampes", minValue = 0f)
                    if (nombreRampesValidation.first) {
                        nombreRampesError = true
                        nombreRampesErrorMessage = nombreRampesValidation.second
                        hasError = true
                    }

                    val nombreBusesValidation = validateNumericField(nombreBusesParRampe, "Nombre de buses", minValue = 0f)
                    if (nombreBusesValidation.first) {
                        nombreBusesParRampeError = true
                        nombreBusesParRampeErrorMessage = nombreBusesValidation.second
                        hasError = true
                    }

                    val pressionValidation = validateNumericField(pressionPulverisation, "Pression", minValue = 0f)
                    if (pressionValidation.first) {
                        pressionPulverisationError = true
                        pressionPulverisationErrorMessage = pressionValidation.second
                        hasError = true
                    }

                    val debitValidation = validateNumericField(debitParBuse, "Débit", minValue = 0f)
                    if (debitValidation.first) {
                        debitParBuseError = true
                        debitParBuseErrorMessage = debitValidation.second
                        hasError = true
                    }

                    val angleValidation = validateNumericField(anglePulverisation, "Angle", minValue = 0f, maxValue = 360f)
                    if (angleValidation.first) {
                        anglePulverisationError = true
                        anglePulverisationErrorMessage = angleValidation.second
                        hasError = true
                    }

                    val largeurValidation = validateNumericField(largeurTraitement, "Largeur", minValue = 0f)
                    if (largeurValidation.first) {
                        largeurTraitementError = true
                        largeurTraitementErrorMessage = largeurValidation.second
                        hasError = true
                    }

                    val vitesseMinValidation = validateNumericField(plageVitesseAvancementMin, "Vitesse min", minValue = 0f)
                    if (vitesseMinValidation.first) {
                        plageVitesseAvancementMinError = true
                        plageVitesseAvancementMinErrorMessage = vitesseMinValidation.second
                        hasError = true
                    }

                    val vitesseMaxValidation = validateNumericField(plageVitesseAvancementMax, "Vitesse max", minValue = 0f)
                    if (vitesseMaxValidation.first) {
                        plageVitesseAvancementMaxError = true
                        plageVitesseAvancementMaxErrorMessage = vitesseMaxValidation.second
                        hasError = true
                    }

                    val volumeValidation = validateNumericField(volumeTotalCuve, "Volume", minValue = 0f)
                    if (volumeValidation.first) {
                        volumeTotalCuveError = true
                        volumeTotalCuveErrorMessage = volumeValidation.second
                        hasError = true
                    }

                    if (!hasError) {
                        try {
                            scope.launch {
                                if (pulverisateurId == null) {
                                    viewModel.addPulverisateur(
                                        nomMateriel = nomMateriel,
                                        modeDeplacement = modeDeplacement,
                                        nombreRampes = nombreRampes.toIntOrNull(),
                                        nombreBusesParRampe = nombreBusesParRampe.toIntOrNull(),
                                        typeBuses = typeBuses,
                                        pressionPulverisation = pressionPulverisation.toFloatOrNull(),
                                        debitParBuse = debitParBuse.toFloatOrNull(),
                                        anglePulverisation = anglePulverisation.toIntOrNull(),
                                        largeurTraitement = largeurTraitement.toFloatOrNull(),
                                        plageVitesseAvancementMin = plageVitesseAvancementMin.toFloatOrNull(),
                                        plageVitesseAvancementMax = plageVitesseAvancementMax.toFloatOrNull(),
                                        volumeTotalCuve = volumeTotalCuve.toIntOrNull()
                                    )
                                } else {
                                    viewModel.updatePulverisateur(
                                        id = pulverisateurId,
                                        nomMateriel = nomMateriel,
                                        modeDeplacement = modeDeplacement,
                                        nombreRampes = nombreRampes.toIntOrNull(),
                                        nombreBusesParRampe = nombreBusesParRampe.toIntOrNull(),
                                        typeBuses = typeBuses,
                                        pressionPulverisation = pressionPulverisation.toFloatOrNull(),
                                        debitParBuse = debitParBuse.toFloatOrNull(),
                                        anglePulverisation = anglePulverisation.toIntOrNull(),
                                        largeurTraitement = largeurTraitement.toFloatOrNull(),
                                        plageVitesseAvancementMin = plageVitesseAvancementMin.toFloatOrNull(),
                                        plageVitesseAvancementMax = plageVitesseAvancementMax.toFloatOrNull(),
                                        volumeTotalCuve = volumeTotalCuve.toIntOrNull()
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
                Text(if (pulverisateurId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 