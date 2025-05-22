package model

import kotlinx.serialization.Serializable

@Serializable
data class Juego(
    val id_juego: Int,
    val nombre: String,
    val descripcion: String,
    val plataforma: String,
    val es_individual: Boolean
)
