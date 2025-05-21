package model

import kotlinx.serialization.Serializable

@Serializable
data class Equipo(
    val nombre: String,
    val victorias: Int = 0,
    val derrotas: Int = 0,
    val idEquipo: Int = 0,
    val fundador: String = "",
    val fechaCreacion: String = "",
    val codigo: String = ""
)

@Serializable
data class EquipoResumen(
    val nombre: String,
    val victorias: Int,
    val derrotas: Int
)

