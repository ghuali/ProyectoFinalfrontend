package network
import ViewModel.SessionManager
import network.NetworkUtils.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import model.LoginRequest
import model.LoginResponse
import model.User

fun apiLogIn(
    email: String,
    password: String,
    callback: (User) -> Unit
) {
    val url = "http://localhost:5000/usuario/login"
    val requestBody = LoginRequest(email, password)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = NetworkUtils.httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val text = response.bodyAsText()
            println("Texto crudo: $text")

            val loginResponse = Json.decodeFromString(LoginResponse.serializer(), text)

            // Agregamos el token al User antes de enviarlo al callback
            val userWithToken = loginResponse.usuario.copy(token = loginResponse.token)

            withContext(Dispatchers.Main) {
                callback(userWithToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                callback(User(0, "Error", "usuario", email, null))
            }
        }
    }
}


fun apiRegister(nombre: String, email: String, password: String, onSuccessResponse: (User) -> Unit) {
    val url = "http://127.0.0.1:5000/usuario/registro"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(nombre, email, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val registerResponse = response.body<RegisterResponse>()
                val user = registerResponse.usuario.copy(token = registerResponse.token)
                onSuccessResponse(user)
            } else {
                println("Error en registro: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Excepción en registro: ${e.message}")
        }
    }
}

fun apiEditUser(
    idUsuario: Int,
    nombre: String? = null,
    correo: String? = null,
    password: String? = null,
    token: String,
    onSuccessResponse: (User) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/usuarios/$idUsuario"

    val requestBody = EditUserRequest(nombre, correo, password)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = httpClient.put(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val updatedUser = response.body<User>()
                onSuccessResponse(updatedUser)
            } else {
                onError("Error: ${response.status}, ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            onError("Excepción en editar usuario: ${e.message}")
        }
    }
}


