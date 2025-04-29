package com.vitizen.app.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vitizen.app.presentation.screen.auth.SignInScreen
import com.vitizen.app.presentation.screen.auth.SignUpScreen
import com.vitizen.app.presentation.screen.auth.SplashScreen
import com.vitizen.app.presentation.screen.auth.TermsAndPrivacyScreen
import com.vitizen.app.presentation.screen.home.HomePage
import com.vitizen.app.presentation.screen.home.section.settings.InformationsGeneralesForm
import com.vitizen.app.presentation.screen.auth.SignInViewModel
import com.vitizen.app.presentation.screen.auth.SignUpViewModel
import com.vitizen.app.presentation.screen.home.section.settings.ParametresViewModel
import com.vitizen.app.data.local.security.SecureCredentialsManager
import com.vitizen.app.presentation.screen.home.section.settings.OperateurForm
import com.vitizen.app.presentation.screen.home.section.settings.PulverisateurForm
import com.vitizen.app.presentation.screen.home.section.settings.ParcelleForm

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
                informationsId = id
            )
        }

        composable("${NavigationRoutes.GENERAL_INFO_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            InformationsGeneralesForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                informationsId = id
            )
        }

        composable(NavigationRoutes.OPERATEUR_FORM) {
            OperateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("${NavigationRoutes.OPERATEUR_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OperateurForm(
                viewModel = parametresViewModel,
                operateurId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.PULVERISATEUR_FORM) {
            PulverisateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("${NavigationRoutes.PULVERISATEUR_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            PulverisateurForm(
                viewModel = parametresViewModel,
                pulverisateurId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.PARCELLE_FORM) {
            ParcelleForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("${NavigationRoutes.PARCELLE_FORM}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ParcelleForm(
                viewModel = parametresViewModel,
                parcelleId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 