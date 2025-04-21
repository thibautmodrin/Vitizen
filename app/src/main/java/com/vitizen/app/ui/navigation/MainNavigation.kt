package com.vitizen.app.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vitizen.app.ui.screen.SignInScreen
import com.vitizen.app.ui.screen.SignUpScreen
import com.vitizen.app.ui.screen.SplashScreen
import com.vitizen.app.ui.screen.TermsAndPrivacyScreen
import com.vitizen.app.ui.screen.HomePage
import com.vitizen.app.ui.screen.InformationsGeneralesForm
import com.vitizen.app.ui.viewmodel.SignInViewModel
import com.vitizen.app.ui.viewmodel.SignUpViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import com.vitizen.app.services.SecureCredentialsManager
import com.vitizen.app.ui.screen.OperateurForm

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainNavigation(
    navController: NavHostController,
    secureCredentialsManager: SecureCredentialsManager
) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val parametresViewModel: ParametresViewModel = hiltViewModel()
    
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

        composable(NavigationRoutes.HOME) {
            HomePage(
                navController = navController,
                parametresViewModel = parametresViewModel,
                onNavigateToProfile = { 
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.HOME) { inclusive = true }
                    }
                },
                onSignOut = { 
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.GENERAL_INFO_FORM) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            InformationsGeneralesForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                infoId = id
            )
        }

        composable("${NavigationRoutes.GENERAL_INFO_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            InformationsGeneralesForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                infoId = id
            )
        }

        composable(NavigationRoutes.OPERATEUR_FORM) { backStackEntry ->
            OperateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                operateurId = null
            )
        }

        composable("${NavigationRoutes.OPERATEUR_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OperateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                operateurId = id
            )
        }
    }
} 