package com.vitizen.app.ui.navigation

object NavigationRoutes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val SPLASH = "splash"
    const val TERMS = "terms"
    const val PRIVACY = "privacy"
    const val PARAMETRES = "parametres"
    const val GENERAL_INFO_FORM = "general_info_form"
    const val OPERATEUR_FORM = "operateur_form"
    const val PULVERISATEUR_FORM = "pulverisateur_form"

    fun operateurFormRoute(id: Long? = null): String {
        return if (id == null) {
            OPERATEUR_FORM
        } else {
            "$OPERATEUR_FORM/$id"
        }
    }

    fun pulverisateurFormRoute(id: Long? = null): String {
        return if (id == null) {
            PULVERISATEUR_FORM
        } else {
            "$PULVERISATEUR_FORM/$id"
        }
    }
} 