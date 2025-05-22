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
    val id_evento: Int,
    val id_juego: Int
)

@Serializable
data class Evento(
    @SerialName("id_evento")
    val idEvento: Int,
    val nombre: String,
    val tipo: String,
    @SerialName("año")
    val anio: Int
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
data class Clasificacion(
    val id_clasificacion: Int,
    val puntos: Int,
    val posicion: Int,
    val usuario: String? = null,
    val equipo: String? = null
)