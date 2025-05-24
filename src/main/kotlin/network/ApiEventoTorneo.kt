package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Clasificacion
import model.Evento
import model.Torneo
import model.TorneoCompleto
import network.NetworkUtils.httpClient


fun getEventos(onSuccessResponse: (List<Evento>) -> Unit) {
    val url = "http://127.0.0.1:5000/eventos"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            if (response.status == HttpStatusCode.OK) {
                val eventos = response.body<List<Evento>>()
                withContext(Dispatchers.Main) {
                    onSuccessResponse(eventos)
                }
            } else {
                println("Error: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}


fun getTorneosPorJuego(juegoId: Int, onSuccessResponse: (List<TorneoCompleto>) -> Unit) {
    val url = "http://127.0.0.1:5000/torneos/completos/$juegoId"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val torneos = response.body<List<TorneoCompleto>>()
                onSuccessResponse(torneos)
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}

fun getTorneosPorEvento(eventoId: Int, onSuccessResponse: (List<TorneoCompleto>) -> Unit) {
    val url = "http://127.0.0.1:5000/torneos/evento/$eventoId"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val torneos = response.body<List<TorneoCompleto>>()
                withContext(Dispatchers.Main) {
                    onSuccessResponse(torneos)
                }
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}

fun getClasificacionPorTorneo(torneoId: Int, onSuccessResponse: (List<Clasificacion>) -> Unit) {
    val url = "http://127.0.0.1:5000/clasificacion/$torneoId"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val clasificacion = response.body<List<Clasificacion>>()
                onSuccessResponse(clasificacion)
            } else {
                println("Error: ${response.status}, Body: $responseBody")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}


