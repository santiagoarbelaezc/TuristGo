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

/**
 * Helper class to manage Google Sign-In using Credential Manager
 */
@Singleton
class GoogleAuthHelper @Inject constructor() {

    suspend fun getGoogleCredential(context: Context): GoogleUserData? {
        val credentialManager = CredentialManager.create(context)
        try {
            val googleClientId = context.getString(R.string.google_web_client_id)
            
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
            
            if (credential is GoogleIdTokenCredential) {
                return GoogleUserData(
                    id = credential.id,
                    email = credential.id, // Usually the id is the email in GoogleIdTokenCredential if requested
                    name = credential.displayName ?: "Google User",
                    photoUrl = credential.profilePictureUri?.toString()
                )
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthHelper", "Error getting Google credential", e)
        }
        return null
    }
}

data class GoogleUserData(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String?
)
