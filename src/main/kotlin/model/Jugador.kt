package model

import kotlinx.serialization.Serializable

@Serializable
data class JugadorResumen(
    val id_usuario: Int,
    val nombre: String,
    val victorias: Int? = null,
    val derrotas: Int? = null
)

@Serializable
data class JugadorTabla(
    val nombre: String,
    val victorias: Int,
    val derrotas: Int
)