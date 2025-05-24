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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.LoginRequest
import model.LoginResponse
import model.User

fun apiLogIn(
    email: String,
    password: String,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
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

            // Intenta parsear como JSON genérico primero
            val jsonElement = Json.parseToJsonElement(text).jsonObject

            if ("error" in jsonElement) {
                val errorMessage = jsonElement["error"]?.jsonPrimitive?.content ?: "Error desconocido"
                withContext(Dispatchers.Main) {
                    onError(errorMessage)
                }
            } else {
                val loginResponse = Json.decodeFromJsonElement(LoginResponse.serializer(), jsonElement)
                val userWithToken = loginResponse.usuario.copy(token = loginResponse.token)

                withContext(Dispatchers.Main) {
                    onSuccess(userWithToken)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError("Error de conexión: ${e.message}")
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
    email: String? = null,
    password: String? = null,
    token: String,
    onSuccessResponse: (User) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/usuarios/editar/$idUsuario"

    val requestBody = EditUserRequest(nombre, email, password)

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


