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
    val id_equipo: Int,
    val nombre: String,
    val victorias: Int,
    val derrotas: Int
)

@Serializable
data class EquipoConCodigo(
    val id_equipo: Int,
    val nombre: String,
    val fundador: Int,
    val fecha_creacion: String,
    val codigo: String,
    val victorias: Int,
    val derrotas: Int
)



