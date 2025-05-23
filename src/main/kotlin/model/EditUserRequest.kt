// model/EditUserRequest.kt
package model

import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val nombre: String? = null,
    val correo: String? = null,
    val password: String? = null
)
