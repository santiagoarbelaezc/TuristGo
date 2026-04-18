package com.turistgo.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.turistgo.app.domain.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHOTO = stringPreferencesKey("user_photo")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userSession: Flow<UserSession> = context.dataStore.data.map { preferences ->
        UserSession(
            userId = preferences[USER_ID],
            name = preferences[USER_NAME],
            email = preferences[USER_EMAIL],
            photoUrl = preferences[USER_PHOTO]?.takeIf { it.isNotEmpty() },
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false
        )
    }

    suspend fun saveSession(userId: String, name: String, email: String, photoUrl: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            preferences[USER_PHOTO] = photoUrl ?: ""
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
