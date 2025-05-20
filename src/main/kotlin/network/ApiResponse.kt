package network
import model.LoginRequest
import model.RegisterRequest
import network.NetworkUtils.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.User


fun apiLogIn(email: String, password: String, onSuccessResponse: (User) -> Unit) {
    val url = "http://127.0.0.1:5000/usuario/login"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val user = response.body<User>()
                onSuccessResponse(user)
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Excepción en login: ${e.message}")
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
                val user = response.body<User>()
                onSuccessResponse(user)
            } else {
                println("Error en registro: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Excepción en registro: ${e.message}")
        }
    }
}


