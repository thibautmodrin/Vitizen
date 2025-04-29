package com.vitizen.app.presentation.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.vitizen.app.presentation.event.UiEvent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import com.vitizen.app.presentation.components.PasswordRequirements
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import com.vitizen.app.presentation.navigation.NavigationRoutes
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText


@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val rgpdAccepted by viewModel.rgpdAccepted.collectAsState()
    var isTransitioning by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    val isPasswordValid = password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() }

    val isConfirmPasswordValid = password == confirmPassword && password.isNotBlank()

    val passwordBorderColor = when {
        password.isBlank() -> MaterialTheme.colorScheme.outline
        !isPasswordValid -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    val confirmPasswordBorderColor = when {
        confirmPassword.isBlank() -> MaterialTheme.colorScheme.outline
        !isConfirmPasswordValid -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is UiEvent.AuthSuccess -> {
                onNavigateToSignIn()
                viewModel.clearEvent()
            }
            is UiEvent.AuthError -> {
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isTransitioning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // En-tête
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        // .background(
                        //     color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        //     shape = MaterialTheme.shapes.medium
                        // )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inscription",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Optimisez vos traitements phytosanitaires grâce à l'analyse de vos données",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                // Mention champs obligatoires
                Text(
                    text = "* Champs obligatoires",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                // Section Informations de connexion
                Text(
                    text = "Informations de connexion",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )

                // Groupe des champs de mot de passe
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // .background(
                        //     color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        //     shape = MaterialTheme.shapes.small
                        // )
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { 
                            Row {
                                Text("Email")
                                Text(
                                    "*",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { 
                            Row {
                                Text(
                                    "Mot de passe",
                                    color = if (password.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                           else if (!isPasswordValid) MaterialTheme.colorScheme.error 
                                           else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "*",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Mot de passe",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = passwordBorderColor,
                            unfocusedBorderColor = passwordBorderColor,
                            focusedLabelColor = if (password.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                              else if (!isPasswordValid) MaterialTheme.colorScheme.error 
                                              else MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = if (password.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                                else if (!isPasswordValid) MaterialTheme.colorScheme.error 
                                                else MaterialTheme.colorScheme.primary
                        ),
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isPasswordValid) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Mot de passe valide",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Cacher le mot de passe" else "Afficher le mot de passe",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .onFocusChanged { isPasswordFocused = it.isFocused },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        )
                    )

                    PasswordRequirements(
                        password = password,
                            isFocused = isPasswordFocused,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        label = { 
                            Row {
                                Text(
                                    "Confirmer le mot de passe",
                                    color = if (confirmPassword.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                           else if (!isConfirmPasswordValid) MaterialTheme.colorScheme.error 
                                           else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "*",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirmer le mot de passe",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = confirmPasswordBorderColor,
                            unfocusedBorderColor = confirmPasswordBorderColor,
                            focusedLabelColor = if (confirmPassword.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                              else if (!isConfirmPasswordValid) MaterialTheme.colorScheme.error 
                                              else MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = if (confirmPassword.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant 
                                                else if (!isConfirmPasswordValid) MaterialTheme.colorScheme.error 
                                                else MaterialTheme.colorScheme.primary
                        ),
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isConfirmPasswordValid) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Confirmation valide",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Cacher le mot de passe" else "Afficher le mot de passe",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                                .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )
                }

                // Fermer le clavier si le mot de passe et sa confirmation sont valides
                LaunchedEffect(isConfirmPasswordValid) {
                    if (isConfirmPasswordValid) {
                        focusManager.clearFocus()
                    }
                }

                // Section Conditions
                Text(
                    text = "Conditions d'utilisation",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                        .padding(top = 16.dp)
                )

                val annotatedText = buildAnnotatedString {
                    append("J'accepte les ")

                    pushStringAnnotation(tag = "CONDITIONS", annotation = "conditions")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("conditions d'utilisation")
                    }
                    pop()

                    append(" et la ")

                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("politique de confidentialité")
                    }
                    pop()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rgpdAccepted,
                        onCheckedChange = { viewModel.updateRgpdAccepted(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ClickableText(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "CONDITIONS", start = offset, end = offset)
                                .firstOrNull()?.let {
                                    navController.navigate(NavigationRoutes.TERMS)
                                }

                            annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                                .firstOrNull()?.let {
                                    navController.navigate(NavigationRoutes.PRIVACY)
                                }
                        }
                    )
                }

                Button(
                    onClick = { viewModel.signUp(email, password, confirmPassword)

                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && rgpdAccepted && isPasswordValid && isConfirmPasswordValid,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "S'inscrire",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }


                TextButton(
                    onClick = {
                        isTransitioning = true
                        viewModel.clearError()
                        viewModel.clearEvent()
                        viewModel.resetForm()
                        onNavigateToSignIn()
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Déjà inscrit ? Se connecter")
                }

                error?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}




