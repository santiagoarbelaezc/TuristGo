package com.turistgo.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.turistgo.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting

/**
 * Service to call Google Gemini API for AI-powered features.
 * Uses the Generative AI SDK with Gemini 1.5 Flash.
 */
object GeminiService {

    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
    )

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            safetySettings = safetySettings
        )
    }

    /**
     * Extracts JSON from a potentially messy AI response string.
     */
    private fun extractJson(text: String): JSONObject? {
        return try {
            val start = text.indexOf("{")
            val end = text.lastIndexOf("}")
            if (start != -1 && end != -1 && start <= end) {
                val jsonPart = text.substring(start, end + 1)
                JSONObject(jsonPart)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Moderates text content using real Gemini AI.
     */
    suspend fun isTextSafe(text: String): SafetyResult = withContext(Dispatchers.IO) {
        try {
            // Si el texto es muy corto o vacío, es seguro por defecto
            if (text.trim().length < 2) return@withContext SafetyResult(true)

            val prompt = """
                Analiza este comentario para TuristGo. 
                Sé permisivo con el lenguaje coloquial, pero RECHAZA (is_safe: false) insultos graves, odio o spam.
                
                Responde en JSON:
                {
                  "is_safe": boolean,
                  "reason": "explicación breve en español si es necesario"
                }

                Texto: "$text"
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: return@withContext SafetyResult(true) // Si falla la IA, permitimos texto por ahora

            val json = extractJson(responseText) ?: return@withContext SafetyResult(true)
            
            SafetyResult(
                isSafe = json.optBoolean("is_safe", true),
                reason = json.optString("reason", "")
            )
        } catch (e: Exception) {
            SafetyResult(true)
        }
    }

    /**
     * Moderates images using real Gemini Vision (Multimodal).
     */
    suspend fun isImageSafe(context: Context, uriString: String): SafetyResult = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            val bitmap = context.contentResolver.openInputStream(uri).use { 
                BitmapFactory.decodeStream(it)
            } ?: return@withContext SafetyResult(true) // Si no carga, permitimos para no bloquear al usuario

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true)

            val prompt = """
                Analiza esta imagen para la app social TuristGo. 
                Tu objetivo es permitir fotos de viajes y lugares, pero BLOQUEAR contenido dañino.
                
                RECHAZA (is_safe: false) SOLO si detectas:
                1. Contenido sexual explícito o desnudos evidentes.
                2. Violencia extrema, sangre real o gore.
                3. Memes ofensivos o basura visual total (spam).
                
                Si la imagen es una foto normal de una persona, un paisaje, comida o el interior de un local, es SEGURA (is_safe: true).
                
                Responde en JSON:
                {
                  "is_safe": boolean,
                  "reason": "explicación en español si se rechaza"
                }
            """.trimIndent()

            val inputContent = content {
                image(scaledBitmap)
                text(prompt)
            }

            val response = generativeModel.generateContent(inputContent)
            val responseText = response.text ?: return@withContext SafetyResult(true)

            val json = extractJson(responseText) ?: return@withContext SafetyResult(true)

            SafetyResult(
                isSafe = json.optBoolean("is_safe", true),
                reason = json.optString("reason", "")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de error técnico real (ej. quota), permitimos por usabilidad
            SafetyResult(true)
        }
    }

    /**
     * Generates a moderator review summary for a given post.
     */
    suspend fun generateModeratorSummary(
        title: String,
        description: String,
        category: String,
        author: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = "Genera un resumen ejecutivo de moderación para este post en TuristGo: Título: $title, Categoría: $category. Analiza su relevancia y da una recomendación."
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No se pudo generar el resumen."
        } catch (e: Exception) {
            "Análisis no disponible temporalmente."
        }
    }

    /**
     * Generates a description for a tourist post based on a title and category using real AI.
     */
    suspend fun generatePostDescription(title: String, category: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Escribe una descripción atractiva y breve (máximo 3 párrafos) para un lugar turístico en Colombia.
                Título del lugar: "$title"
                Categoría: "$category"
                Estilo: Inspirador, útil para viajeros y profesional.
                No incluyas etiquetas ni introducciones, responde directamente con la descripción.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "Ven y descubre las maravillas de $title, un destino imperdible en la categoría de $category."
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback en caso de error de red o cuota
            "$title es un destino fascinante en la categoría de $category que ofrece experiencias únicas a sus visitantes."
        }
    }
}

data class SafetyResult(
    val isSafe: Boolean,
    val reason: String? = null
)
