package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.TreatmentViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentScreen(
    viewModel: TreatmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    
    // États locaux pour les champs du formulaire
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var produit by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var commentaire by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau traitement") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = produit,
                onValueChange = { produit = it },
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = surface,
                onValueChange = { surface = it },
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = commentaire,
                onValueChange = { commentaire = it },
                label = { Text("Commentaire") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    viewModel.submitTreatment(
                        date = date,
                        type = type,
                        produit = produit,
                        surface = surface,
                        commentaire = commentaire
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider")
            }
        }
    }
} 