package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Torneo(
    val id_torneo: Int,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val ubicacion: String,
    val id_evento: Int?,
    val id_juego: Int
)

@Serializable
data class Evento(
    val id_evento: Int,
    val nombre: String,
    val tipo: String,   // "anual" o "mensual"
    val año: Int,
    val mes: Int? = null
)

@Serializable
data class TorneoCreateRequest(
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val ubicacion: String,
    val id_evento: Int,
    val id_juego: Int
)

@Serializable
data class TorneoCompleto(
    val id_torneo: Int,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val ubicacion: String,
    val id_evento: Int? = null,
    val nombre_evento: String? = null
)

@Serializable
data class Clasificacion(
    val id_clasificacion: Int,
    val puntos: Int,
    val posicion: Int?,   // Nullable Int para aceptar null del JSON
    val usuario: String? = null,
    val equipo: String? = null
)