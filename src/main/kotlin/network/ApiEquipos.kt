package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Equipo
import model.EquipoResumen
import network.NetworkUtils.httpClient

fun getEquipos(onSuccessResponse: (List<Equipo>) -> Unit) {
    val url = "http://127.0.0.1:5000/equipos"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val equipos = response.body<List<Equipo>>()
                onSuccessResponse(equipos)
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}

fun getEquiposPorJuego(idJuego: Int, onSuccessResponse: (List<EquipoResumen>) -> Unit) {
    val url = "http://127.0.0.1:5000/equipos/por-juego/$idJuego"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            if (response.status == HttpStatusCode.OK) {
                val equipos = response.body<List<EquipoResumen>>()
                onSuccessResponse(equipos)
            } else {
                println("Error: ${response.status}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}



