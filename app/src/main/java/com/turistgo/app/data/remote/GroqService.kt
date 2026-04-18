package com.turistgo.app.data.remote

import com.turistgo.app.data.remote.model.GroqRequest
import com.turistgo.app.data.remote.model.GroqResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: GroqRequest
    ): GroqResponse
}
