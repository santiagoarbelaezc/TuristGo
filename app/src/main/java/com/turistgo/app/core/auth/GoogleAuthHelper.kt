package com.turistgo.app.core.auth

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.turistgo.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthHelper @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun getGoogleCredential(context: Context): Result<GoogleUserData?> {
        val credentialManager = CredentialManager.create(context)
        return try {
            val googleClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(googleClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(request = request, context = context)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken

                // Autenticar en Firebase con el token de Google
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val firebaseResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = firebaseResult.user
                    ?: return Result.failure(Exception("Firebase no retornó usuario"))

                val locale = extractLocaleFromIdToken(idToken)

                Result.success(
                    GoogleUserData(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: credential.id,
                        name = firebaseUser.displayName ?: "Google User",
                        photoUrl = firebaseUser.photoUrl?.toString(),
                        locale = locale
                    )
                )
            } else {
                Result.failure(Exception("Tipo de credencial no soportado"))
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthHelper", "Error getting Google credential", e)
            Result.failure(e)
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
