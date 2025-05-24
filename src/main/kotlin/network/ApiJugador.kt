package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.JugadorResumen
import network.NetworkUtils.httpClient


fun getJugadoresPorJuego(idJuego: Int, onSuccessResponse: (List<JugadorResumen>) -> Unit) {
    val url = "http://127.0.0.1:5000/jugadores/por-juego/$idJuego"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            if (response.status == HttpStatusCode.OK) {
                val jugadores = response.body<List<JugadorResumen>>()
                onSuccessResponse(jugadores)
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}

fun unirseJuegoIndividual(idJuego: Int, token: String, onSuccessResponse: () -> Unit) {
    val url = "http://127.0.0.1:5000/unirse/juego-individual"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("id_juego" to idJuego))
            }
            if (response.status == HttpStatusCode.Created) {
                onSuccessResponse()
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}

fun salirJuegoIndividual(idJuego: Int, token: String, onSuccessResponse: () -> Unit) {
    val url = "http://127.0.0.1:5000/salir/juego-individual"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("id_juego" to idJuego))
            }
            if (response.status == HttpStatusCode.OK) {
                onSuccessResponse()
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}
