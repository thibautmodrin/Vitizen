package com.vitizen.app.ui.navigation

object NavigationRoutes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val SPLASH = "splash"
    const val TERMS = "terms"
    const val PRIVACY = "privacy"
    const val PULVERISATEUR_FORM = "pulverisateur_form/{nom}"

    fun pulverisateurFormRoute(nom: String) = "pulverisateur_form/$nom"
} 