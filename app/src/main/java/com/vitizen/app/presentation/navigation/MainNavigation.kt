package com.vitizen.app.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.vitizen.app.presentation.screen.home.section.settings.ParcellesView

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
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomePage(
                navController = navController,
                parametresViewModel = parametresViewModel,
                onNavigateToProfile = { 
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onSignOut = { 
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                viewModel = signInViewModel,
                signUpViewModel = signUpViewModel,
                secureCredentialsManager = secureCredentialsManager,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                viewModel = signUpViewModel,
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Terms.route) {
            TermsAndPrivacyScreen(
                navController = navController,
                isPrivacyPolicy = false
            )
        }

        composable(Screen.Privacy.route) {
            TermsAndPrivacyScreen(
                navController = navController,
                isPrivacyPolicy = true
            )
        }

        composable(Screen.GeneralInfoForm.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            InformationsGeneralesForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                informationsId = id
            )
        }

        composable(Screen.GeneralInfoForm.routeWithId) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            InformationsGeneralesForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.navigateUp() },
                informationsId = id
            )
        }

        composable(Screen.OperateurForm.route) {
            OperateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OperateurForm.routeWithId) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            OperateurForm(
                viewModel = parametresViewModel,
                operateurId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PulverisateurForm.route) {
            PulverisateurForm(
                viewModel = parametresViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PulverisateurForm.routeWithId) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            PulverisateurForm(
                viewModel = parametresViewModel,
                pulverisateurId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Parcelles.route) {
            val parcelles by parametresViewModel.parcelles.collectAsState()
            ParcellesView(
                parcelles = parcelles,
                onParcelleAdded = { parcelle ->
                    parametresViewModel.addParcelle(parcelle)
                },
                onParcelleDeleted = { parcelle ->
                    parametresViewModel.deleteParcelle(parcelle)
                }
            )
        }

        composable(
            route = Screen.ParcelleForm.route,
            arguments = listOf(
                navArgument("parcelleId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val parcelleId = backStackEntry.arguments?.getString("parcelleId")
            val parcelle = if (parcelleId != "new") parametresViewModel.getParcelleById(parcelleId!!) else null

            ParcelleForm(
                parcelle = parcelle,
                onSave = { parcelle ->
                    if (parcelle.id.isEmpty()) {
                        parametresViewModel.addParcelle(parcelle)
                    } else {
                        parametresViewModel.updateParcelle(parcelle)
                    }
                    navController.popBackStack()
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Terms : Screen("terms")
    object Privacy : Screen("privacy")
    object GeneralInfoForm : Screen("general_info_form") {
        val routeWithId = "$route/{id}"
    }
    object OperateurForm : Screen("operateur_form") {
        val routeWithId = "$route/{id}"
    }
    object PulverisateurForm : Screen("pulverisateur_form") {
        val routeWithId = "$route/{id}"
    }
    object Parcelles : Screen("parcelles")
    object ParcelleForm : Screen("parcelle_form/{parcelleId}") {
        fun createRoute(parcelleId: String? = null) = "parcelle_form/${parcelleId ?: "new"}"
    }
} 