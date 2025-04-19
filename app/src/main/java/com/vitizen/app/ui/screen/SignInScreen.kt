package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.component.CitizenScaffold
import com.vitizen.app.ui.component.MessageType
import com.vitizen.app.ui.event.ErrorType
import com.vitizen.app.ui.viewmodel.SignInViewModel
import com.vitizen.app.ui.state.AuthState
import com.vitizen.app.ui.event.UiEvent
import com.vitizen.app.ui.navigation.NavigationRoutes
import com.vitizen.app.services.SecureCredentialsManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import com.vitizen.app.ui.component.Logo
import com.vitizen.app.ui.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.widget.Toast
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import com.vitizen.app.ui.event.ToastDuration
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import com.vitizen.app.ui.theme.VitizenTheme
import com.vitizen.app.services.FirstConnectionManager

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    secureCredentialsManager: SecureCredentialsManager,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showResetEmailDialog by remember { mutableStateOf(false) }
    var showResetConfirmationDialog by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val justSignedUp by signUpViewModel.justSignedUp.collectAsState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Charger les identifiants sauvegardés
    LaunchedEffect(Unit) {
        val (savedEmail, savedPassword) = secureCredentialsManager.getCredentials()
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            email = savedEmail
            password = savedPassword
            rememberMe = true
        }
    }

    // Préremplir l'email après une inscription réussie
    LaunchedEffect(justSignedUp) {
        if (justSignedUp) {
            email = signUpViewModel.email.value
            password = ""
            showConfirmationDialog = true
            // Marquer comme première connexion
            FirstConnectionManager.resetFirstConnection(context)
            // Vérifier que la première connexion est bien marquée
            Log.d("SignInScreen", "justSignedUp: $justSignedUp, isFirstConnection: ${FirstConnectionManager.isFirstConnection(context)}")
            signUpViewModel.clearJustSignedUp()
        }
    }

    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is UiEvent.AuthSuccess -> {
                if ((uiEvent as UiEvent.AuthSuccess).message.contains("réinitialisation")) {
                    showResetEmailDialog = true
                } else {
                    isTransitioning = true
                    if (rememberMe) {
                        scope.launch {
                            secureCredentialsManager.saveCredentials(email, password)
                        }
                    } else {
                        scope.launch {
                            secureCredentialsManager.clearCredentials()
                        }
                    }
                    // Ajouter un délai pour montrer la transition
                    delay(1000)
                    onNavigateToHome()
                }
                viewModel.clearEvent()
            }
            is UiEvent.AuthError -> {
                viewModel.clearEvent()
            }
            is UiEvent.ShowToast -> {
                val toast = Toast.makeText(
                    context,
                    (uiEvent as UiEvent.ShowToast).message,
                    if ((uiEvent as UiEvent.ShowToast).duration == ToastDuration.Short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
                )
                toast.show()
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false
                                 signUpViewModel.resetForm()
                               },
            title = {
                Text(
                    text = "Inscription réussie",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "Un email de confirmation a été envoyé à votre adresse. Veuillez vérifier votre boîte de réception avant de vous connecter.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false
                                signUpViewModel.resetForm()
                    }
                ) {
                    Text("J'ai compris")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showResetEmailDialog) {
        AlertDialog(
            onDismissRequest = { showResetEmailDialog = false },
            title = {
                Text(
                    text = "Demande de réinitialisation envoyée",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    Text(
                        text = "Un email de réinitialisation a été envoyé à votre adresse.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Veuillez vérifier votre boîte de réception et suivre les instructions pour réinitialiser votre mot de passe.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showResetEmailDialog = false }
                ) {
                    Text("J'ai compris")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showResetConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmationDialog = false },
            title = {
                Text(
                    text = "Confirmation de réinitialisation",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "Si l'adresse $email existe dans notre système, vous recevrez un email de réinitialisation de mot de passe.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirmationDialog = false
                        viewModel.sendPasswordResetEmail(email)
                    }
                ) {
                    Text("Envoyer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmationDialog = false }
                ) {
                    Text("Annuler")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        if (isTransitioning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Logo(
                        modifier = Modifier.padding(bottom = 146.dp)
                    )
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 300.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo(
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Connexion",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Cacher le mot de passe" else "Afficher le mot de passe"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                TextButton(
                    onClick = {
                        if (email.isBlank()) {
                            viewModel.signIn("", "", false)
                        } else {
                            showResetConfirmationDialog = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Mot de passe oublié ?")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        text = "Se souvenir de moi",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Button(
                    onClick = { viewModel.signIn(email, password, rememberMe) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Se connecter",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                TextButton(
                    onClick = {
                        isTransitioning = true
                        viewModel.clearError()
                        viewModel.clearEvent()
                        onNavigateToSignUp()
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Pas encore inscrit ? Créer un compte")
                }

                error?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    VitizenTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo(
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Connexion",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = "exemple@email.com",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = "••••••••",
                    onValueChange = {},
                    label = { Text("Mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Afficher le mot de passe"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                TextButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Mot de passe oublié ?")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = {}
                    )
                    Text(
                        text = "Se souvenir de moi",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Se connecter",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Pas encore inscrit ? Créer un compte")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenTransitionPreview() {
    VitizenTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo(
                    modifier = Modifier.padding(bottom = 146.dp)
                )
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 300.dp)
                )
            }
        }
    }
}

