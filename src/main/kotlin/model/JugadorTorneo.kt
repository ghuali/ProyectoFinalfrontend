package model

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioTorneoRequest(
    val torneoId: Int
)

@Serializable
data class MensajeResponse(
    val mensaje: String? = null,
    val error: String? = null
)


