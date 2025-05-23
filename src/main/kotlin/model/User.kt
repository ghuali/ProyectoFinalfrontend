package model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val nombre: String,
    val rol: String,
    val email: String,
    val token: String? = null
)

@Serializable
data class Usuario(
    val id_usuario: Int,
    val nombre: String
)