package model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val contraseña: String
)

@Serializable
data class RegisterResponse(
    val token: String,
    val usuario: User
)
