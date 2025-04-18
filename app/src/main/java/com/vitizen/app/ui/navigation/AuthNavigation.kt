package com.vitizen.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitizen.app.ui.screen.HomePage
import com.vitizen.app.ui.screen.SignInScreen
import com.vitizen.app.ui.screen.SignUpScreen
import com.vitizen.app.ui.screen.SplashScreen
import com.vitizen.app.ui.screen.TermsAndPrivacyScreen
import com.vitizen.app.ui.viewmodel.SignInViewModel
import com.vitizen.app.ui.viewmodel.SignUpViewModel
import com.vitizen.app.services.SecureCredentialsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
                        popUpTo(NavigationRoutes.SPLASH) { inclusive = true }
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
                        popUpTo(NavigationRoutes.SIGN_IN) { inclusive = true }
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

        composable(NavigationRoutes.HOME) {
            HomePage(
                onNavigateToProfile = {
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.HOME) { inclusive = true }
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