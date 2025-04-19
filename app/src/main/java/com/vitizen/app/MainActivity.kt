package com.vitizen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vitizen.app.ui.navigation.MainNavigation
import com.vitizen.app.ui.navigation.NavigationManager
import com.vitizen.app.ui.theme.VitizenTheme
import com.vitizen.app.services.SecureCredentialsManager
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
                    modifier = Modifier.fillMaxSize(),
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