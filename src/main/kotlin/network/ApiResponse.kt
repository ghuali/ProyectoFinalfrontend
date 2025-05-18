package network
import model.LoginRequest
import network.NetworkUtils.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.User
import utils.sha512


fun apiLogIn(email: String, password: String, onSucessResponse: (User) -> Unit) {
    val url = "http://127.0.0.1:5000/usuario/login"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val user = response.body<User>()
                onSucessResponse(user)
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Excepción en login: ${e.message}")
        }
    }
}

