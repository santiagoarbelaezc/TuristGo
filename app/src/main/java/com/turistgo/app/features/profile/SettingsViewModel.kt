package com.turistgo.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.core.locale.LanguageState
import com.turistgo.app.core.theme.ThemeState
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: AppDataRepository
) : ViewModel() {

    fun setLanguage(language: AppLanguage) {
        LanguageState.current.value = language
        // In a real app, we'd persist this to DataStore or User profile in DB
    }

    fun setTheme(dark: Boolean?) {
        ThemeState.isDarkMode.value = dark
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onComplete()
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            val session = sessionManager.userSession.first()
            if (session.isLoggedIn && session.userId != null) {
                repository.deleteUser(session.userId)
                sessionManager.clearSession()
                onComplete()
            }
        }
    }
}
