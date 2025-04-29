package com.vitizen.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vitizen.app.presentation.screen.auth.SignInScreen
import com.vitizen.app.presentation.screen.auth.SignUpScreen
import com.vitizen.app.presentation.screen.auth.SplashScreen
import com.vitizen.app.presentation.screen.auth.TermsAndPrivacyScreen
import com.vitizen.app.presentation.screen.auth.SignInViewModel
import com.vitizen.app.presentation.screen.auth.SignUpViewModel
import com.vitizen.app.data.local.security.SecureCredentialsManager

@Composable
fun AuthNavigation(
    navController: NavHostController,
    secureCredentialsManager: SecureCredentialsManager
) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.SPLASH
    ) {
        composable(NavigationRoutes.SPLASH) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.SIGN_IN) {
            SignInScreen(
                viewModel = signInViewModel,
                signUpViewModel = signUpViewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {
                    navController.navigate(NavigationRoutes.SIGN_UP) {
                        popUpTo(NavigationRoutes.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.SIGN_UP) {
            SignUpScreen(
                navController = navController,
                viewModel = signUpViewModel,
                onNavigateToSignIn = {
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.TERMS) {
            TermsAndPrivacyScreen(
                navController = navController,
                isPrivacyPolicy = false
            )
        }

        composable(NavigationRoutes.PRIVACY) {
            TermsAndPrivacyScreen(
                navController = navController,
                isPrivacyPolicy = true
            )
        }
    }
} 