package com.vitizen.app.data.local.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vitizen.app.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager @Inject constructor(
    private val context: Context,
    private val roomDataSource: RoomDataSource
) {
    private val userKey = stringPreferencesKey("user")

    suspend fun saveUser(user: User) {
        if (user.uid.isBlank()) {
            throw Exception("UID utilisateur invalide")
        }
        context.dataStore.edit { preferences ->
            preferences[userKey] = user.uid
        }
        // Assurer que l'utilisateur est aussi sauvegardé dans Room
        roomDataSource.insertUser(user)
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(userKey)
        }
        // Nettoyer aussi les données de Room
        roomDataSource.clearUsers()
    }

    suspend fun getUser(): User? {
        return context.dataStore.data.map { preferences ->
            preferences[userKey]
        }.first()?.let { uid ->
            if (uid.isBlank()) {
                null
            } else {
                // Récupérer l'utilisateur depuis RoomDataSource avec l'uid stocké
                roomDataSource.getUserByUid(uid)
            }
        }
    }

    suspend fun saveUserUid(uid: String) {
        if (uid.isBlank()) {
            throw Exception("UID utilisateur invalide")
        }
        context.dataStore.edit { preferences ->
            preferences[userKey] = uid
        }
    }
}