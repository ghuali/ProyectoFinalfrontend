package model

import kotlinx.serialization.Serializable

@Serializable
data class Jugador(
    val nombre: String,
    val victorias: String,
    val derrotas: String,
    val personaje: String
)