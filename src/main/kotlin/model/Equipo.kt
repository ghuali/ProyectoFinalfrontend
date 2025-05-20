package model

data class Equipo(
    val nombre: String,
    val victorias: String = "0",
    val derrotas: String = "0",
    val idEquipo: Int = 0,
    val fundador: Int = 0,
    val fechaCreacion: String = "",
    val codigo: String = ""
)

data class EquipoResumen(
    val nombre: String,
    val victorias: String,
    val derrotas: Int
)

