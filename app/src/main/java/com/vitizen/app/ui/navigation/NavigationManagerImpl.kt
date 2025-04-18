package com.vitizen.app.ui.navigation

import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManagerImpl @Inject constructor() : NavigationManager {
    private lateinit var navController: NavController

    override fun initializeNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun navigateTo(route: String) {
        navController.navigate(route)
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun clearBackStack() {
        navController.popBackStack(navController.graph.startDestinationId, false)
    }
} 