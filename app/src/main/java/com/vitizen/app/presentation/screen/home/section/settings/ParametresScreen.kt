package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import com.vitizen.app.data.local.entity.OperateurEntity
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.data.local.entity.PulverisateurEntity

data class TabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToForm: (String) -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Infos", Icons.Default.Info),
        TabItem("Opérateurs", Icons.Default.Person),
        TabItem("Pulvérisateurs", Icons.Default.Build),
        TabItem("Parcelles", Icons.Default.Landscape)
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { 
                Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    },
                    icon = { Icon(tab.icon, contentDescription = tab.title) }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTabIndex) {
                0 -> InformationsBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                1 -> OperateursBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                2 -> PulverisateursBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
                3 -> ParcellesBox(
                    viewModel = viewModel,
                    onNavigateToForm = onNavigateToForm
                )
            }
        }
    }
}

@Composable
fun InformationsBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val informationsGenerales by viewModel.informationsGenerales.collectAsState()
    val isEditingGeneralInfo by viewModel.isEditingGeneralInfo.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

            Spacer(modifier = Modifier.height(16.dp))

                                    if (informationsGenerales.isNotEmpty()) {
                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                    items(informationsGenerales) { info ->
                        InformationItem(
                            information = info,
                            isEditing = isEditingGeneralInfo,
                            onEdit = { onNavigateToForm("generalInfo/${info.id}") },
                            onDelete = { viewModel.deleteInformationsGenerales(info) }
                        )
                                            }
                                        }
                                    } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                                        Text(
                                            text = "Aucune information générale enregistrée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

@Composable
fun OperateursBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val operateurs by viewModel.operateurs.collectAsState()
    val isEditingOperator by viewModel.isEditingOperator.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

            Spacer(modifier = Modifier.height(16.dp))

                                    if (operateurs.isNotEmpty()) {
                LazyColumn(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                    items(operateurs) { operateur ->
                        OperateurItem(
                            operateur = operateur,
                            isEditing = isEditingOperator,
                            onEdit = { onNavigateToForm("operateur/${operateur.id}") },
                            onDelete = { viewModel.deleteOperateur(operateur) }
                        )
                                            }
                                        }
                                    } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                                        Text(
                                            text = "Aucun opérateur enregistré",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
}

@Composable
fun PulverisateursBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val pulverisateurs by viewModel.pulverisateurs.collectAsState()
    var isEditingPulverisateur by remember { mutableStateOf(false) }
    var pulverisateurToDelete by remember { mutableStateOf<PulverisateurEntity?>(null) }

    LaunchedEffect(pulverisateurToDelete) {
        pulverisateurToDelete?.let { pulverisateur ->
            viewModel.deletePulverisateur(pulverisateur)
            pulverisateurToDelete = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                        IconButton(
                    onClick = { isEditingPulverisateur = !isEditingPulverisateur }
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

            Spacer(modifier = Modifier.height(16.dp))

                                    if (pulverisateurs.isNotEmpty()) {
                LazyColumn(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                    items(pulverisateurs) { pulverisateur ->
                        PulverisateurItem(
                            pulverisateur = pulverisateur,
                            isEditing = isEditingPulverisateur,
                            onEdit = { onNavigateToForm("pulverisateur/${pulverisateur.id}") },
                            onDelete = { pulverisateurToDelete = pulverisateur }
                        )
                                            }
                                        }
                                    } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                                        Text(
                                            text = "Aucun pulvérisateur enregistré",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
}

@Composable
fun ParcellesBox(
    viewModel: ParametresViewModel,
    onNavigateToForm: (String) -> Unit
) {
    val parcelles by viewModel.parcelles.collectAsState()
    var isEditingParcelle by remember { mutableStateOf(false) }
    var parcelleToDelete by remember { mutableStateOf<ParcelleEntity?>(null) }

    LaunchedEffect(parcelleToDelete) {
        parcelleToDelete?.let { parcelle ->
            viewModel.deleteParcelle(parcelle)
            parcelleToDelete = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                        IconButton(
                    onClick = { isEditingParcelle = !isEditingParcelle }
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

            Spacer(modifier = Modifier.height(16.dp))

            if (parcelles.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parcelles) { parcelle ->
                        ParcelleItem(
                            parcelle = parcelle,
                            isEditing = isEditingParcelle,
                            onEdit = { onNavigateToForm("parcelle/${parcelle.id}") },
                            onDelete = { parcelleToDelete = parcelle }
                        )
                    }
                }
            } else {
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
                                        }
                                    }
                                }
                            }

@Composable
fun InformationItem(
    information: InformationsGeneralesEntity,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditing) { if (isEditing) onEdit() },
        color = if (isEditing) 
            MaterialTheme.colorScheme.surfaceVariant
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = information.nomDomaine,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${information.modeCulture} • ${information.surfaceTotale} ha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Code postal: ${information.codePostal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (information.certifications.isNotEmpty()) {
                    Text(
                        text = "Certifications: ${information.certifications.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isEditing) {
                IconButton(onClick = onDelete) {
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

@Composable
fun OperateurItem(
    operateur: OperateurEntity,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditing) { if (isEditing) onEdit() },
        color = if (isEditing) 
            MaterialTheme.colorScheme.surfaceVariant
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .padding(16.dp)
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = operateur.nom,
                    style = MaterialTheme.typography.titleMedium
                )
                if (operateur.diplomes.isNotEmpty()) {
                                                            Text(
                        text = "Diplômes: ${operateur.diplomes.joinToString(", ")}",
                                                                style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (operateur.materielMaitrise.isNotEmpty()) {
                    Text(
                        text = "Matériel maîtrisé: ${operateur.materielMaitrise.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (operateur.disponibleWeekend) "Disponible le weekend" else "Non disponible le weekend",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isEditing) {
                IconButton(onClick = onDelete) {
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

@Composable
fun PulverisateurItem(
    pulverisateur: PulverisateurEntity,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ce pulvérisateur ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditing) { if (isEditing) onEdit() },
        color = if (isEditing) 
            MaterialTheme.colorScheme.surfaceVariant
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pulverisateur.nomMateriel,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Mode: ${pulverisateur.modeDeplacement}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type buses: ${pulverisateur.typeBuses}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                                        Text(
                    text = "Largeur: ${pulverisateur.largeurTraitement}m • Volume: ${pulverisateur.volumeTotalCuve}L",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isEditing) {
                IconButton(onClick = { showDeleteConfirmation = true }) {
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

@Composable
fun ParcelleItem(
    parcelle: ParcelleEntity,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cette parcelle ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditing) { if (isEditing) onEdit() },
        color = if (isEditing) 
            MaterialTheme.colorScheme.surfaceVariant
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = parcelle.nom,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${parcelle.surface} ha • ${parcelle.cepage}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type: ${parcelle.typeConduite} • Plantation: ${parcelle.anneePlantation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (parcelle.zoneSensible || parcelle.zoneHumide || parcelle.drainage || parcelle.enherbement) {
                    Text(
                        text = listOfNotNull(
                            if (parcelle.zoneSensible) "Zone sensible" else null,
                            if (parcelle.zoneHumide) "Zone humide" else null,
                            if (parcelle.drainage) "Drainage" else null,
                            if (parcelle.enherbement) "Enherbement" else null
                        ).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isEditing) {
                IconButton(onClick = { showDeleteConfirmation = true }) {
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

