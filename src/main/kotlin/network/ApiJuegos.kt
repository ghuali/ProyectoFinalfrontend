package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Juego
import network.NetworkUtils.httpClient

fun getJuegosPorEquipo(onSuccessResponse: (List<Juego>) -> Unit) {
    val url = "http://127.0.0.1:5000/juegos?tipo=equipo"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val juegos = response.body<List<Juego>>()
                onSuccessResponse(juegos)
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}fun getJuegosIndividuales(onSuccessResponse: (List<Juego>) -> Unit) {
    val url = "http://127.0.0.1:5000/juegos?tipo=individual"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val juegos = response.body<List<Juego>>()
                onSuccessResponse(juegos)
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}
