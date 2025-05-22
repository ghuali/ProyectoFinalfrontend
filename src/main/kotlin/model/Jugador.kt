package model

import kotlinx.serialization.Serializable

@Serializable
data class JugadorResumen(
    val nombre: String = "",
    val victorias: Int = 0,
    val derrotas: Int = 0
)