package com.turistgo.app.core.utils

/**
 * Utility object containing the official list of Colombian Departments and Cities
 * provided for the TuristGo application.
 */
object ColombiaGeography {
    val data: Map<String, List<String>> = mapOf(
        "Antioquia" to listOf("Medellín", "Bello", "Itagüí", "Envigado", "Rionegro", "Apartadó"),
        "Atlántico" to listOf("Barranquilla", "Soledad", "Malambo", "Puerto Colombia"),
        "Bogotá D.C." to listOf("Bogotá"),
        "Bolívar" to listOf("Cartagena", "Magangué", "Turbaco"),
        "Boyacá" to listOf("Tunja", "Duitama", "Sogamoso"),
        "Caldas" to listOf("Manizales", "Villamaría", "La Dorada"),
        "Caquetá" to listOf("Florencia", "San Vicente del Caguán"),
        "Cauca" to listOf("Popayán", "Santander de Quilichao"),
        "Cesar" to listOf("Valledupar", "Aguachica"),
        "Chocó" to listOf("Quibdó", "Istmina"),
        "Córdoba" to listOf("Montería", "Lorica", "Sahagún"),
        "Cundinamarca" to listOf("Soacha", "Zipaquirá", "Chía", "Facatativá", "Girardot"),
        "Guajira" to listOf("Riohacha", "Maicao", "Uribia"),
        "Huila" to listOf("Neiva", "Pitalito"),
        "Magdalena" to listOf("Santa Marta", "Ciénaga"),
        "Meta" to listOf("Villavicencio", "Acacías"),
        "Nariño" to listOf("Pasto", "Tumaco"),
        "Norte de Santander" to listOf("Cúcuta", "Ocaña"),
        "Quindío" to listOf("Armenia", "Calarcá"),
        "Risaralda" to listOf("Pereira", "Dosquebradas"),
        "Santander" to listOf("Bucaramanga", "Floridablanca", "Girón", "Piedecuesta", "Barrancabermeja"),
        "Sucre" to listOf("Sincelejo", "Corozal"),
        "Tolima" to listOf("Ibagué", "Espinal"),
        "Valle del Cauca" to listOf("Cali", "Palmira", "Buenaventura", "Tuluá", "Cartago"),
        "Arauca" to listOf("Arauca"),
        "Casanare" to listOf("Yopal"),
        "Putumayo" to listOf("Mocoa"),
        "San Andrés y Providencia" to listOf("San Andrés"),
        "Amazonas" to listOf("Leticia"),
        "Guaviare" to listOf("San José del Guaviare"),
        "Vaupés" to listOf("Mitú"),
        "Vichada" to listOf("Puerto Carreño")
    )

    fun getDepartments(): List<String> = data.keys.sorted()

    fun getCities(department: String): List<String> = data[department]?.sorted() ?: emptyList()
}
