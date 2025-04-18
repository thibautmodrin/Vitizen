package com.example.vitizen.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vitizen.ui.viewmodel.SignUpViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var viewModel: SignUpViewModel

    @Test
    fun testSignUpScreenDisplaysCorrectly() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToSignIn = {},
                onNavigateToHome = {}
            )
        }

        composeTestRule.onNodeWithText("Inscription").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nom").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mot de passe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirmer le mot de passe").assertIsDisplayed()
    }

    @Test
    fun testSignUpWithValidCredentials() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToSignIn = {},
                onNavigateToHome = {}
            )
        }

        // TODO: Implémenter la logique de test pour l'inscription
    }

    @Test
    fun testSignUpWithInvalidCredentials() {
        MockitoAnnotations.openMocks(this)
        
        composeTestRule.setContent {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToSignIn = {},
                onNavigateToHome = {}
            )
        }

        // TODO: Implémenter la logique de test pour les erreurs d'inscription
    }
} 