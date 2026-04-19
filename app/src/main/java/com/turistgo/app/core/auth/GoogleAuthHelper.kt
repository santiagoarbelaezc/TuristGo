package com.turistgo.app.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.turistgo.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import android.util.Log
import android.util.Base64
import org.json.JSONObject

/**
 * Helper class to manage Google Sign-In using Credential Manager
 */
@Singleton
class GoogleAuthHelper @Inject constructor() {

    suspend fun getGoogleCredential(context: Context): Result<GoogleUserData?> {
        val credentialManager = CredentialManager.create(context)
        try {
            val googleClientId = com.turistgo.app.BuildConfig.GOOGLE_WEB_CLIENT_ID
            
            // Check if it's still a placeholder
            if (googleClientId.contains("YOUR_GOOGLE_CLIENT_ID")) {
                return Result.failure(Exception("Error de configuración: Debes configurar un Web Client ID válido en strings.xml"))
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(googleClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            
            return if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken
                val locale = extractLocaleFromIdToken(idToken)
                
                Result.success(
                    GoogleUserData(
                        id = credential.id,
                        email = credential.id,
                        name = credential.displayName ?: "Google User",
                        photoUrl = credential.profilePictureUri?.toString(),
                        locale = locale
                    )
                )
            } else {
                Result.failure(Exception("Tipo de credencial no soportado"))
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthHelper", "Error getting Google credential", e)
            return Result.failure(e)
        }
    }

    private fun extractLocaleFromIdToken(idToken: String): String? {
        return try {
            val parts = idToken.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val json = JSONObject(payload)
            json.optString("locale", null)
        } catch (e: Exception) {
            null
        }
    }
}

data class GoogleUserData(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val locale: String? = null
)
