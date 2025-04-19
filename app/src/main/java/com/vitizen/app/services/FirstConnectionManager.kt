package com.vitizen.app.services

import android.content.Context
import android.util.Log

object FirstConnectionManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_FIRST_CONNECTION = "first_connection"

    fun isFirstConnection(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirst = prefs.getBoolean(KEY_FIRST_CONNECTION, true)
        Log.d("FirstConnectionManager", "isFirstConnection: $isFirst")
        return isFirst
    }

    fun setFirstConnectionDone(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_CONNECTION, false).apply()
        Log.d("FirstConnectionManager", "setFirstConnectionDone: First connection marked as done (set to false)")
    }

    fun resetFirstConnection(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_CONNECTION, true).apply()
        Log.d("FirstConnectionManager", "resetFirstConnection: First connection reset to true")
    }
} 