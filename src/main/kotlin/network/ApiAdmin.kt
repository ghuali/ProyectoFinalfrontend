package network

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EventoCreateRequest
import model.MensajeResponse
import model.TorneoCreateRequest
import network.NetworkUtils.httpClient


fun apiCrearEvento(
    evento: EventoCreateRequest,
    token: String?,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/evento/crear"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val jsonBody = Json.encodeToString(evento)

            val response: HttpResponse = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                setBody(jsonBody)
            }

            val text = response.bodyAsText()
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val mensajeResponse = Json.decodeFromString(MensajeResponse.serializer(), text)
                withContext(Dispatchers.Main) {
                    onSuccess(mensajeResponse.mensaje ?: "Evento creado correctamente")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError("Error: ${response.status}, $text")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Excepción: ${e.message}")
            }
        }
    }
}


fun apiCrearTorneo(
    nombre: String,
    fechaInicio: String,  // en formato "YYYY-MM-DD"
    fechaFin: String,     // en formato "YYYY-MM-DD"
    ubicacion: String,
    idEvento: Int?,       // opcional
    idJuego: Int,
    token: String?,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/torneo/crear"

    val torneo = TorneoCreateRequest(
        nombre = nombre,
        fecha_inicio = fechaInicio,
        fecha_fin = fechaFin,
        ubicacion = ubicacion,
        id_juego = idJuego,
        id_evento = idEvento
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                setBody(torneo) // <- directamente la data class
            }

            val text = response.bodyAsText()
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val mensajeResponse = Json.decodeFromString(MensajeResponse.serializer(), text)
                withContext(Dispatchers.Main) {
                    onSuccess(mensajeResponse.mensaje ?: "Torneo creado correctamente")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError("Error: ${response.status}, $text")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Excepción: ${e.message}")
            }
        }
    }
}