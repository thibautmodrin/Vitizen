package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.animation.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn


@Composable
fun SectionCard(
    title: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onAddClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onExpandToggle
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Replier" else "Déplier"
                    )
                    if (isExpanded) {
                        IconButton(
                            onClick = onAddClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Ajouter"
                            )
                        }
                    }
                }
            }

            if (isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToForm: (String) -> Unit,
) {
    // État local pour gérer l'expansion des sections

    var isPulverisateurExpanded by remember { mutableStateOf(false) }
    var isEditingPulverisateur by remember { mutableStateOf(false) }
    var isParcelleExpanded by remember { mutableStateOf(false) }
    var isEditingParcelle by remember { mutableStateOf(false) }
    val informationsGenerales by viewModel.informationsGenerales.collectAsState()
    val isGeneralInfoExpanded by viewModel.isGeneralInfoExpanded.collectAsState()
    val isEditingGeneralInfo by viewModel.isEditingGeneralInfo.collectAsState()
    val operateurs by viewModel.operateurs.collectAsState()
    val isOperatorExpanded by viewModel.isOperatorExpanded.collectAsState()
    val isEditingOperator by viewModel.isEditingOperator.collectAsState()
    val pulverisateurs by viewModel.pulverisateurs.collectAsState()
    val parcelles by viewModel.parcelles.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Informations générales
                item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setGeneralInfoExpanded(!isGeneralInfoExpanded) }
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Informations générales",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isGeneralInfoExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isGeneralInfoExpanded) "Replier" else "Déplier"
                                )
                                if (isGeneralInfoExpanded) {
                                    IconButton(
                                        onClick = {
                                                if (isEditingGeneralInfo) {
                                                    viewModel.stopEditingGeneralInfo()
                                                } else {
                                                    viewModel.startEditingGeneralInfo()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isEditingGeneralInfo) Icons.Default.Save else Icons.Default.Edit,
                                                contentDescription = if (isEditingGeneralInfo) "Sauvegarder" else "Modifier"
                                            )
                                        }
                                        if (isEditingGeneralInfo) {
                                            IconButton(
                                                onClick = { onNavigateToForm("generalInfo") }
                                    ) {
                                        Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Ajouter des informations"
                                        )
                                            }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isGeneralInfoExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                    if (informationsGenerales.isNotEmpty()) {
                                        Column(
                                    modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            informationsGenerales.forEach { info ->
                                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                                    onClick = { 
                                                        if (isEditingGeneralInfo) {
                                                            onNavigateToForm("generalInfo/${info.id}")
                                                        }
                                                    },
                                                    enabled = isEditingGeneralInfo,
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isEditingGeneralInfo) 
                                                            MaterialTheme.colorScheme.surface 
                                                        else 
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (isEditingGeneralInfo) 
                                                            MaterialTheme.colorScheme.onSurface 
                                                        else 
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .padding(16.dp)
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "${info.nomDomaine} • ${info.modeCulture} • ${info.surfaceTotale} ha • ${info.codePostal} • ${info.certifications.joinToString(", ")}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                        
                                                        if (isEditingGeneralInfo) {
                                                            IconButton(
                                                                onClick = {
                                                                    scope.launch {
                                                                        viewModel.deleteInformationsGenerales(info)
                                                                    }
                                                                },
                                                                modifier = Modifier
                                                                    .align(Alignment.CenterEnd)
                                                                    .padding(8.dp)
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
                                    } else {
                                        Text(
                                            text = "Aucune information générale enregistrée",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Opérateur de traitement
                item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setOperatorExpanded(!isOperatorExpanded) }
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                    text = "Opérateur de traitement",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                        imageVector = if (isOperatorExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isOperatorExpanded) "Replier" else "Déplier"
                                )
                                    if (isOperatorExpanded) {
                                    IconButton(
                                        onClick = {
                                                if (isEditingOperator) {
                                                    viewModel.stopEditingOperator()
                                                } else {
                                                    viewModel.startEditingOperator()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isEditingOperator) Icons.Default.Save else Icons.Default.Edit,
                                                contentDescription = if (isEditingOperator) "Sauvegarder" else "Modifier"
                                            )
                                        }
                                        if (isEditingOperator) {
                                            IconButton(
                                                onClick = { onNavigateToForm("operateur") }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Ajouter un opérateur"
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = isOperatorExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (operateurs.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            operateurs.forEach { operateur ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                                    onClick = { 
                                                        if (isEditingOperator) {
                                                            onNavigateToForm("operateur/${operateur.id}")
                                                        }
                                                    },
                                                    enabled = isEditingOperator,
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isEditingOperator) 
                                                            MaterialTheme.colorScheme.surface 
                                                        else 
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (isEditingOperator) 
                                                            MaterialTheme.colorScheme.onSurface 
                                                        else 
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .padding(16.dp)
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "${operateur.nom} • ${if (operateur.disponibleWeekend) "Disponible weekend" else "Non disponible weekend"} • ${operateur.diplomes.joinToString(", ")} • ${operateur.materielMaitrise.joinToString(", ")}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                        
                                                        if (isEditingOperator) {
                                                            IconButton(
                                                                onClick = {
                                                                    scope.launch {
                                                                        viewModel.deleteOperateur(operateur)
                                                                    }
                                                                },
                                                                modifier = Modifier
                                                                    .align(Alignment.CenterEnd)
                                                                    .padding(8.dp)
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
                                    } else {
                                        Text(
                                            text = "Aucun opérateur enregistré",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Matériel de traitement
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isPulverisateurExpanded = !isPulverisateurExpanded }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Matériel de traitement",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPulverisateurExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isPulverisateurExpanded) "Replier" else "Déplier"
                                    )
                                    if (isPulverisateurExpanded) {
                                        IconButton(
                                            onClick = {
                                                if (isEditingPulverisateur) {
                                                    isEditingPulverisateur = false
                                                } else {
                                                    isEditingPulverisateur = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isEditingPulverisateur) Icons.Default.Save else Icons.Default.Edit,
                                                contentDescription = if (isEditingPulverisateur) "Sauvegarder" else "Modifier"
                                            )
                                        }
                                        if (isEditingPulverisateur) {
                                            IconButton(
                                                onClick = { onNavigateToForm("pulverisateur") }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Ajouter un pulvérisateur"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                                visible = isPulverisateurExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                    if (pulverisateurs.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            pulverisateurs.forEach { pulverisateur ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        onClick = { 
                                                        if (isEditingPulverisateur) {
                                                            onNavigateToForm("pulverisateur/${pulverisateur.id}")
                                            }
                                        },
                                                    enabled = isEditingPulverisateur,
                                        colors = CardDefaults.cardColors(
                                                        containerColor = if (isEditingPulverisateur) 
                                                            MaterialTheme.colorScheme.surface 
                                                        else 
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (isEditingPulverisateur) 
                                                            MaterialTheme.colorScheme.onSurface 
                                                        else 
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                                        Row(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                                text = "${pulverisateur.nomMateriel} • ${pulverisateur.modeDeplacement} • ${pulverisateur.nombreRampes} rampes • ${pulverisateur.nombreBusesParRampe} buses/rampe • ${pulverisateur.typeBuses}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                        
                                                        if (isEditingPulverisateur) {
                                                IconButton(
                                                                onClick = {
                                                                    scope.launch {
                                                                        viewModel.deletePulverisateur(pulverisateur)
                                                                    }
                                                                },
                                                    modifier = Modifier
                                                                    .align(Alignment.CenterEnd)
                                                        .padding(8.dp)
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
                                    } else {
                                        Text(
                                            text = "Aucun pulvérisateur enregistré",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Parcelles
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isParcelleExpanded = !isParcelleExpanded }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Parcelles",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isParcelleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isParcelleExpanded) "Replier" else "Déplier"
                                    )
                                    if (isParcelleExpanded) {
                                        IconButton(
                                            onClick = {
                                                if (isEditingParcelle) {
                                                    isEditingParcelle = false
                                                } else {
                                                    isEditingParcelle = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isEditingParcelle) Icons.Default.Save else Icons.Default.Edit,
                                                contentDescription = if (isEditingParcelle) "Sauvegarder" else "Modifier"
                                            )
                                        }
                                        if (isEditingParcelle) {
                                            IconButton(
                                                onClick = { onNavigateToForm("parcelle") }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Ajouter une parcelle"
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = isParcelleExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (parcelles.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            parcelles.forEach { parcelle ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                                    onClick = { 
                                                        if (isEditingParcelle) {
                                                            onNavigateToForm("parcelle/${parcelle.id}")
                                                        }
                                                    },
                                                    enabled = isEditingParcelle,
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isEditingParcelle) 
                                                            MaterialTheme.colorScheme.surface 
                                                        else 
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (isEditingParcelle) 
                                                            MaterialTheme.colorScheme.onSurface 
                                                        else 
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .padding(16.dp)
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "${parcelle.nom} • ${parcelle.surface} ha • ${parcelle.cepage} • ${parcelle.anneePlantation} • ${parcelle.typeConduite}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                        
                                                        if (isEditingParcelle) {
                                                            IconButton(
                                                                onClick = {
                                                                    scope.launch {
                                                                        viewModel.deleteParcelle(parcelle)
                                                                    }
                                                                },
                                                                modifier = Modifier
                                                                    .align(Alignment.CenterEnd)
                                                                    .padding(8.dp)
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
                                    } else {
                                        Text(
                                            text = "Aucune parcelle enregistrée",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    )
} 

