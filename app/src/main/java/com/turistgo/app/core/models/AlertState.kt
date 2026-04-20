package com.turistgo.app.core.models

/**
 * Representa los tipos de alertas disponibles en el sistema.
 */
enum class AlertType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

/**
 * Estado que define el contenido y visibilidad de un modal de alerta.
 */
data class AlertState(
    val title: String = "",
    val message: String = "",
    val type: AlertType = AlertType.ERROR,
    val isVisible: Boolean = false,
    val onConfirm: (() -> Unit)? = null
)
