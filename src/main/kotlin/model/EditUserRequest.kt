// model/EditUserRequest.kt
package model

import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null
)
