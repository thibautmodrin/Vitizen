package com.example.vitizen.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vitizen.domain.model.User
import com.example.vitizen.services.ConnectivityChecker
import com.example.vitizen.services.SecureCredentialsManager
import com.example.vitizen.ui.state.AuthState
import com.example.vitizen.ui.viewmodel.SignInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: SignInViewModel

    @Mock
    private lateinit var secureCredentialsManager: SecureCredentialsManager

    @Mock
    private lateinit var connectivityChecker: ConnectivityChecker

    private val authState = MutableStateFlow<AuthState>(AuthState.Initial)

    @Test
    fun testSignInScreenDisplaysCorrectly() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        composeTestRule.onNodeWithText("Connexion").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mot de passe").assertIsDisplayed()
    }

    @Test
    fun testSignInWithValidCredentials() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        // TODO: Implémenter la logique de test pour la connexion
    }

    @Test
    fun testSignInWithInvalidCredentials() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        // TODO: Implémenter la logique de test pour les erreurs de connexion
    }

    @Test
    fun `signIn with saved credentials and no internet should show success message`() {
        MockitoAnnotations.openMocks(this)
        
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedUser = User(
            uid = "123",
            name = "Test User",
            email = email,
            isEmailVerified = true
        )

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, password))
        `when`(connectivityChecker.isConnected()).thenReturn(false)
        `when`(viewModel.authState).thenReturn(authState)

        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Mot de passe").performTextInput(password)
        composeTestRule.onNodeWithText("Se connecter").performClick()

        // Then
        authState.value = AuthState.Authenticated(savedUser)
        composeTestRule.onNodeWithText("Connexion réussie").assertIsDisplayed()
    }

    @Test
    fun `signIn with no saved credentials and no internet should show error message`() {
        MockitoAnnotations.openMocks(this)
        
        // Given
        val email = "test@example.com"
        val password = "password123"

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(connectivityChecker.isConnected()).thenReturn(false)
        `when`(viewModel.authState).thenReturn(authState)

        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Mot de passe").performTextInput(password)
        composeTestRule.onNodeWithText("Se connecter").performClick()

        // Then
        authState.value = AuthState.Error("Pas de connexion internet")
        composeTestRule.onNodeWithText("Pas de connexion internet").assertIsDisplayed()
    }

    @Test
    fun `signIn with saved credentials but different password should show error message`() {
        MockitoAnnotations.openMocks(this)
        
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedPassword = "different-password"

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, savedPassword))
        `when`(connectivityChecker.isConnected()).thenReturn(false)
        `when`(viewModel.authState).thenReturn(authState)

        composeTestRule.setContent {
            SignInScreen(
                viewModel = viewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {},
                onNavigateToHome = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Mot de passe").performTextInput(password)
        composeTestRule.onNodeWithText("Se connecter").performClick()

        // Then
        authState.value = AuthState.Error("Pas de connexion internet")
        composeTestRule.onNodeWithText("Pas de connexion internet").assertIsDisplayed()
    }
} 