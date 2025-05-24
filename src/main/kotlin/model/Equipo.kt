package model

import kotlinx.serialization.Serializable

@Serializable
data class Equipo(
    val id_equipo: Int,
    val nombre: String,
    val victorias: Int? = null,
    val derrotas: Int? = null,
    val fundador: Int? = null,
    val fecha_creacion: String? = null
)

@Serializable
data class EquipoResumen(
    val nombre: String,
    val victorias: Int,
    val derrotas: Int
)

