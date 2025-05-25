import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.UsuarioTorneoRequest
import model.MensajeResponse
import network.NetworkUtils.httpClient

fun apiEntrarTorneoJugadorIndividual(
    torneoId: Int,
    token: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/inscribir/jugador"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val jsonBody = Json.encodeToString(mapOf("id_torneo" to torneoId))

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
                    onSuccess(mensajeResponse.mensaje ?: "Entrado al torneo correctamente")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError("Error: ${response.status}, ${text}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Excepción: ${e.message}")
            }
        }
    }
}

fun apiSalirTorneoJugadorIndividual(
    torneoId: Int,
    token: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/torneo/$torneoId/salir"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                // No body needed
            }

            val text = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val mensajeResponse = Json.decodeFromString(MensajeResponse.serializer(), text)
                withContext(Dispatchers.Main) {
                    onSuccess(mensajeResponse.mensaje ?: "Saliste del torneo correctamente")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError("Error: ${response.status}, ${text}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Excepción: ${e.message}")
            }
        }
    }
}

fun apiInscribirEquipoEnTorneo(
    torneoId: Int,
    equipoId: Int,
    token: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/inscribir/equipo"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val jsonBody = Json.encodeToString(
                mapOf(
                    "id_torneo" to torneoId,
                    "id_equipo" to equipoId
                )
            )

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
                    onSuccess(mensajeResponse.mensaje ?: "Equipo inscrito correctamente")
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

fun apiSalirTorneoEquipo(
    torneoId: Int,
    equipoId: Int,
    token: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val url = "http://127.0.0.1:5000/torneo-equipo/$torneoId/salir"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                setBody("""{ "id_equipo": $equipoId }""")
            }

            val text = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                val mensajeResponse = Json.decodeFromString(MensajeResponse.serializer(), text)
                withContext(Dispatchers.Main) {
                    onSuccess(mensajeResponse.mensaje ?: "El equipo ha salido del torneo correctamente")
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

