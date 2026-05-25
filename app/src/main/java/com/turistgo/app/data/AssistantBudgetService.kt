package com.turistgo.app.data

import com.turistgo.app.domain.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssistantBudgetService @Inject constructor() {

    /**
     * Detecta si el mensaje del usuario indica que tiene bajo presupuesto.
     */
    fun isLowBudget(message: String): Boolean {
        val keywords = listOf(
            "poco presupuesto", "bajo presupuesto", "barato", "economico", 
            "sin dinero", "no tengo plata", "no tengo mucho presupuesto", 
            "ahorrar", "bajo costo", "gratis", "gratuito", "economicos", 
            "baratos", "poca plata", "presupuesto bajo", "presupuesto limitado"
        )
        val normalized = message.lowercase()
        return keywords.any { normalized.contains(it) }
    }

    /**
     * Filtra los lugares del catálogo para priorizar aquellos en la ciudad del usuario
     * y descartar los costosos.
     */
    fun filterPlacesForLowBudget(posts: List<Post>, userCity: String?, userDepartment: String?): List<Post> {
        return posts.filter { post ->
            // 1. Filtrar por cercanía (misma ciudad o departamento para no gastar en viajes largos)
            val isLocal = if (!userCity.isNullOrBlank()) {
                post.city?.contains(userCity, ignoreCase = true) == true || 
                post.location.contains(userCity, ignoreCase = true)
            } else if (!userDepartment.isNullOrBlank()) {
                post.department?.contains(userDepartment, ignoreCase = true) == true || 
                post.location.contains(userDepartment, ignoreCase = true)
            } else {
                true // Si no hay info de ubicación del usuario, se permite
            }

            // 2. Filtrar si es un lugar costoso
            val isExpensive = isPriceExpensive(post.priceRange)

            isLocal && !isExpensive
        }
    }

    private fun isPriceExpensive(priceRange: String): Boolean {
        val normalized = priceRange.lowercase()
        if (normalized.isBlank() || normalized.contains("no disponible") || normalized.contains("gratis")) return false
        
        // Indicadores textuales de costoso
        if (normalized.contains("$$$") || normalized.contains("caro") || normalized.contains("alto")) return true

        // Extraer números para validar si supera un umbral razonable para bajo presupuesto (ej. > 100k COP)
        val numberRegex = "\\d+".toRegex()
        val numbers = numberRegex.findAll(normalized).map { it.value.toIntOrNull() }.filterNotNull().toList()
        if (numbers.isNotEmpty()) {
            val maxPrice = numbers.maxOrNull() ?: 0
            if (maxPrice > 100000) return true
        }

        return false
    }

    /**
     * Retorna las instrucciones del prompt de sistema adicionales para limitar a 1 día y local.
     */
    fun getLowBudgetContext(userCity: String?): String {
        return """
            ⚠️ REGLA DE PRESUPUESTO BAJO DETECTADA:
            El usuario ha indicado que tiene poco presupuesto.
            - Debes generar un itinerario de un solo día (Día 1 únicamente).
            - No le sugieras destinos lejanos a su ubicación (${userCity ?: "su ciudad"}).
            - Propón actividades gratuitas o de bajo costo (parques públicos, caminatas, etc.).
            - Sé empático y aclara que el plan fue optimizado a 1 día local por cuestiones de presupuesto.
        """.trimIndent()
    }
}
