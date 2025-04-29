package com.vitizen.app.presentation.navigation.manager

import androidx.navigation.NavController

interface NavigationManager {
    fun initializeNavigation(navController: NavController)
    fun navigateTo(route: String)
    fun navigateBack()
    fun clearBackStack()
} 