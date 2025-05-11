package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Historial(
    @SerialName("nombre") var nombre: String,
    @SerialName("descripcion") var descripcion: String
)
