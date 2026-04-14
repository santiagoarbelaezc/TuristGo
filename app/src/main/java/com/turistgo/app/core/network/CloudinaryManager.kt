package com.turistgo.app.core.network

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object CloudinaryManager {
    suspend fun uploadImage(context: Context, fileUri: Uri): String? {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(fileUri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        continuation.resume(url)
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(null)
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resume(null)
                    }
                }).dispatch()
        }
    }
}
