package com.turistgo.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String = "llama-3.3-70b-versatile",
    val temperature: Double = 0.7,
    @SerialName("max_tokens") val maxTokens: Int = 1024,
    @SerialName("top_p") val topP: Double = 1.0,
    val stream: Boolean = false,
    @SerialName("stop") val stop: String? = null
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val id: String,
    val choices: List<GroqChoice>,
    val created: Long,
    val model: String,
    val usage: GroqUsage
)

@Serializable
data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    @SerialName("finish_reason") val finishReason: String
)

@Serializable
data class GroqUsage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int
)
