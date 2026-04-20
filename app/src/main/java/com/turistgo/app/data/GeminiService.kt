package com.turistgo.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

/**
 * Service to call Google Gemini API for AI-powered features.
 * Uses the free Gemini 2.0 Flash endpoint.
 */
object GeminiService {

    // Replace with your actual Gemini API key
    private const val API_KEY = "AIzaSyCdummy_replace_with_real_key"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    /**
     * Generates a MOCK moderator review summary for a given post.
     * Simulates an AI response without real API calls for demonstration purposes.
     */
    suspend fun generateModeratorSummary(
        title: String,
        description: String,
        category: String,
        author: String
    ): String = withContext(Dispatchers.IO) {
        kotlinx.coroutines.delay(1500) // Simular tiempo de procesamiento
        
        """
            1. **Puntos Clave**: La publicación describe detalladamente el sitio "$title". Proporciona información útil sobre acceso y experiencia del visitante.
            2. **Nivel de Relevancia**: **Alta**. El contenido es original y aporta valor a la comunidad de TuristGo.
            3. **Acciones Recomendadas**: **Aprobar**. La información no infringe ninguna norma y parece ser verídica basada en patrones históricos.
            
            _Análisis generado por el simulador de IA (Gemini Mockup)._
        """.trimIndent()
    }

    /**
     * Moderates text content (comments, descriptions) to check for toxicity or inappropriate language.
     */
    suspend fun isTextSafe(text: String): SafetyResult = withContext(Dispatchers.IO) {
        delay(1000) // Simular análisis
        
        // Simulación: Palabras prohibidas
        val forbidden = listOf("tonto", "estúpido", "idiota", "spam", "basura", "fuck", "shit")
        val found = forbidden.filter { text.lowercase().contains(it) }
        
        if (found.isNotEmpty()) {
            SafetyResult(
                isSafe = false,
                reason = "Tu mensaje contiene lenguaje inapropiado (${found.joinToString(", ")}). Por favor, mantén el respeto en la comunidad."
            )
        } else {
            SafetyResult(isSafe = true)
        }
    }

    /**
     * Moderates images to check for suggestive or inappropriate content.
     */
    suspend fun isImageSafe(uri: Any): SafetyResult = withContext(Dispatchers.IO) {
        delay(1500) // Simular análisis de visión por computadora
        
        // Simulación: En un entorno real llamaríamos a la API Vision de Gemini
        // Aquí simulamos un éxito el 95% de las veces, para demostrar el bloqueo
        // Si el URI contiene la palabra "inappropriate" simulamos un fallo
        val uriString = uri.toString().lowercase()
        if (uriString.contains("suggestive") || uriString.contains("inappropriate") || uriString.contains("nude")) {
            SafetyResult(
                isSafe = false,
                reason = "La imagen detectada contiene contenido sugestivo o inapropiado que infringe nuestras normas."
            )
        } else {
            SafetyResult(isSafe = true)
        }
    }

    /**
     * Generates a MOCK description for a tourist post based on a title and category.
     */
    suspend fun generatePostDescription(title: String, category: String): String = withContext(Dispatchers.IO) {
        delay(1500) // Simular procesamiento de IA
        
        val templates = mapOf(
            "Gastronomía" to "Descubre $title, un rincón gastronómico imperdible que ofrece una experiencia culinaria única. Con sabores auténticos de la región, ambiente acogedor y atención personalizada, es el lugar ideal para disfrutar en familia o con amigos. Sus especialidades locales y precios razonables lo hacen destacar entre los mejores de la zona.",
            "Cultura" to "$title es un espacio cultural que invita a explorar la riqueza artística e histórica de la región. Con exhibiciones permanentes y temporales, actividades educativas y un entorno inspirador, este sitio es un punto de encuentro para amantes del arte, la historia y la identidad local.",
            "Naturaleza" to "$title te conecta con lo mejor de la naturaleza colombiana. Un refugio de biodiversidad donde podrás disfrutar de paisajes impresionantes, fauna silvestre y senderos bien trazados. Ideal para senderismo, fotografía y quienes buscan desconectarse del ritmo urbano.",
            "Entretenimiento" to "$title es el lugar perfecto para la diversión y el entretenimiento. Ambiente moderno, propuesta variada y una atmósfera vibrante que garantiza una experiencia memorable. Perfecta para grupos, celebraciones or simplemente para disfrutar de una buena noche.",
            "Historia" to "$title es un testigo vivo de la historia de la región. Con una arquitectura imponente y relatos que atraviesan siglos, este lugar ofrece una conexión directa con el pasado. Ideal para recorridos guiados y amantes del patrimonio cultural."
        )
        
        templates[category] ?: "$title es un punto de interés turístico que vale la pena visitar. Ofrece una experiencia única en su tipo, con características que lo hacen destacar en la región. Recomendado para turistas y locales que buscan nuevas experiencias."
    }
}

data class SafetyResult(
    val isSafe: Boolean,
    val reason: String? = null
)
