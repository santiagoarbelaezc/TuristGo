package com.turistgo.app.data

import com.turistgo.app.domain.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssistantBudgetService @Inject constructor() {

    private val cityCoordinates = mapOf(
        "armenia" to Pair(4.535, -75.672),
        "medellin" to Pair(6.244, -75.589),
        "bogota" to Pair(4.711, -74.072),
        "cali" to Pair(3.451, -76.531),
        "cartagena" to Pair(10.399, -75.514),
        "ipiales" to Pair(0.821, -77.644),
        "manizales" to Pair(5.068, -75.517),
        "guatape" to Pair(6.233, -75.161),
        "montenegro" to Pair(4.535, -75.750),
        "quimbaya" to Pair(4.622, -75.795),
        "santa marta" to Pair(11.240, -74.199),
        "pereira" to Pair(4.813, -75.696),
        "ibague" to Pair(4.438, -75.232),
        "bucaramanga" to Pair(7.125, -73.119),
        "barranquilla" to Pair(10.968, -74.781),
        "yopal" to Pair(5.337, -72.395),
        "neiva" to Pair(2.927, -75.281),
        "pasto" to Pair(1.213, -77.281),
        "popayan" to Pair(2.441, -76.606),
        "tunja" to Pair(5.535, -73.367),
        "florencia" to Pair(1.614, -75.606),
        "quibdo" to Pair(5.692, -76.658),
        "riohacha" to Pair(11.544, -72.906),
        "villavicencio" to Pair(4.142, -73.626),
        "sincelejo" to Pair(9.300, -75.399),
        "arauca" to Pair(7.084, -70.759),
        "mocoa" to Pair(1.147, -76.646),
        "san andres" to Pair(12.584, -81.700),
        "leticia" to Pair(-4.215, -69.940),
        "san jose del guaviare" to Pair(2.566, -72.642),
        "mitu" to Pair(1.198, -70.173),
        "puerto carreno" to Pair(6.189, -67.485)
    )

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
        val hasKeyword = keywords.any { normalized.contains(it) }
        
        val extracted = extractBudget(message)
        val isExplicitLow = extracted != null && extracted < 50000
        
        return hasKeyword || isExplicitLow
    }

    /**
     * Extrae el valor numérico del presupuesto si es especificado en pesos.
     * Retorna null si no se detecta presupuesto explícito.
     */
    fun extractBudget(message: String): Int? {
        val normalized = message.lowercase()
            .replace(".", "")
            .replace(",", "")
        
        val budgetKeywords = listOf("presupuesto", "dinero", "tengo", "plata", "pesos", "$", "mil", "k", "cop")
        if (budgetKeywords.any { normalized.contains(it) }) {
            val numberRegex = "\\d+".toRegex()
            val matches = numberRegex.findAll(normalized).map { it.value.toIntOrNull() }.filterNotNull().toList()
            for (num in matches) {
                // Si el número es pequeño y dice "mil" o "k" después, lo multiplicamos por 1000
                if (num in 1..999) {
                    val index = normalized.indexOf(num.toString())
                    val substring = normalized.substring(index)
                    if (substring.contains("mil") || substring.contains("k")) {
                        return num * 1000
                    }
                }
                // Si es mayor o igual a 1000, asumimos el valor directo
                if (num >= 1000) {
                    return num
                }
            }
        }
        return null
    }

    /**
     * Filtra los lugares del catálogo para priorizar aquellos en la ciudad del usuario
     * y descartar los costosos.
     */
    fun filterPlacesForLowBudget(posts: List<Post>, userCity: String?, userDepartment: String?): List<Post> {
        return posts.filter { post ->
            val isLocal = if (!userCity.isNullOrBlank()) {
                post.city?.contains(userCity, ignoreCase = true) == true || 
                post.location.contains(userCity, ignoreCase = true)
            } else if (!userDepartment.isNullOrBlank()) {
                post.department?.contains(userDepartment, ignoreCase = true) == true || 
                post.location.contains(userDepartment, ignoreCase = true)
            } else {
                true
            }

            val isExpensive = isPriceExpensive(post.priceRange)
            isLocal && !isExpensive
        }
    }

    /**
     * Filtra los lugares del catálogo y los ordena de menor a mayor distancia
     * con base en la ubicación del usuario (lat/lng) y menor costo.
     */
    fun getClosestCheapestPlaces(
        posts: List<Post>,
        userCity: String?,
        userDepartment: String?
    ): List<Post> {
        val userCoords = getUserCoordinates(userCity, userDepartment)
        
        return posts.filter { it.status == com.turistgo.app.domain.model.PostStatus.APPROVED }
            .sortedWith(compareBy<Post> { post ->
                if (post.latitude != null && post.longitude != null) {
                    calculateDistance(
                        userCoords.first, userCoords.second,
                        post.latitude, post.longitude
                    )
                } else {
                    9999.0
                }
            }.thenBy {
                extractMaxPrice(it.priceRange)
            })
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radio de la tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun getUserCoordinates(city: String?, department: String?): Pair<Double, Double> {
        val normalizedCity = city?.lowercase()?.trim() ?: ""
        val normalizedDept = department?.lowercase()?.trim() ?: ""
        
        cityCoordinates[normalizedCity]?.let { return it }
        
        if (normalizedDept.contains("antioquia")) return Pair(6.244, -75.589)
        if (normalizedDept.contains("bogota")) return Pair(4.711, -74.072)
        if (normalizedDept.contains("valle")) return Pair(3.451, -76.531)
        if (normalizedDept.contains("quindio")) return Pair(4.535, -75.672)
        if (normalizedDept.contains("bolivar")) return Pair(10.399, -75.514)
        if (normalizedDept.contains("caldas")) return Pair(5.068, -75.517)
        if (normalizedDept.contains("magdalena")) return Pair(11.240, -74.199)
        if (normalizedDept.contains("nariño") || normalizedDept.contains("narino")) return Pair(1.213, -77.281)
        
        return Pair(4.711, -74.072)
    }

    private fun isPriceExpensive(priceRange: String): Boolean {
        val normalized = priceRange.lowercase()
        if (normalized.isBlank() || normalized.contains("no disponible") || normalized.contains("gratis") || normalized.contains("libre")) return false
        if (normalized.contains("$$$") || normalized.contains("caro") || normalized.contains("alto")) return true

        val numberRegex = "\\d+".toRegex()
        val numbers = numberRegex.findAll(normalized).map { it.value.toIntOrNull() }.filterNotNull().toList()
        if (numbers.isNotEmpty()) {
            val maxPrice = numbers.maxOrNull() ?: 0
            if (maxPrice > 100000) return true
        }
        return false
    }

    private fun extractMaxPrice(priceRange: String): Int {
        val normalized = priceRange.lowercase()
        if (normalized.contains("gratis") || normalized.contains("libre") || normalized.contains("entrada libre")) return 0
        val numberRegex = "\\d+".toRegex()
        val numbers = numberRegex.findAll(normalized).map { it.value.toIntOrNull() }.filterNotNull().toList()
        return numbers.maxOrNull() ?: 100000
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
