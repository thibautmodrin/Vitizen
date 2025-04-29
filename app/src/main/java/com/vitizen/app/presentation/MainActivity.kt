package com.vitizen.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vitizen.app.data.local.security.SecureCredentialsManager
import com.vitizen.app.presentation.navigation.MainNavigation
import com.vitizen.app.presentation.navigation.manager.NavigationManager
import com.vitizen.app.presentation.theme.VitizenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var secureCredentialsManager: SecureCredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            VitizenTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navigationManager.initializeNavigation(navController)
                    MainNavigation(
                        navController = navController,
                        secureCredentialsManager = secureCredentialsManager
                    )
                }
            }
        }
    }
}