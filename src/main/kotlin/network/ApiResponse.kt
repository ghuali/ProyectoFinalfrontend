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
import kotlinx.coroutines.withContext
import model.User
import utils.sha512


fun apiLogIn(
    email: String,
    password: String,
    onSuccessResponse: (User) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/usuario/login"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val user = response.body<User>()
                withContext(Dispatchers.Main) {
                    onSuccessResponse(user)
                }
            } else {
                val error = response.bodyAsText()
                withContext(Dispatchers.Main) {
                    onError("Error: ${response.status}. $error")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Excepción: ${e.localizedMessage}")
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


