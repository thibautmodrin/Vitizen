package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthState()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    when (state) {
        is SplashViewModel.SplashState.Authenticated -> {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }
        is SplashViewModel.SplashState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                onNavigateToAuth()
            }
        }
        else -> {}
    }
} 