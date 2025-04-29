package com.vitizen.app.presentation.screen.home.section.record

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentScreen(
    viewModel: TreatmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TreatmentViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is TreatmentViewModel.UiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = uiState.date,
            onValueChange = { viewModel.updateDate(it) },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.fieldErrors.containsKey("date"),
            supportingText = {
                if (uiState.fieldErrors.containsKey("date")) {
                    Text(
                        text = uiState.fieldErrors["date"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = uiState.type,
            onValueChange = { viewModel.updateType(it) },
            label = { Text("Type de traitement") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.fieldErrors.containsKey("type"),
            supportingText = {
                if (uiState.fieldErrors.containsKey("type")) {
                    Text(
                        text = uiState.fieldErrors["type"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = uiState.produit,
            onValueChange = { viewModel.updateProduit(it) },
            label = { Text("Produit utilisé") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.fieldErrors.containsKey("produit"),
            supportingText = {
                if (uiState.fieldErrors.containsKey("produit")) {
                    Text(
                        text = uiState.fieldErrors["produit"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = uiState.surface,
            onValueChange = { viewModel.updateSurface(it) },
            label = { Text("Hectares traités") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.fieldErrors.containsKey("surface"),
            supportingText = {
                if (uiState.fieldErrors.containsKey("surface")) {
                    Text(
                        text = uiState.fieldErrors["surface"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = uiState.commentaire,
            onValueChange = { viewModel.updateCommentaire(it) },
            label = { Text("Commentaire") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.submitTreatment() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Valider")
        }
    }
} 