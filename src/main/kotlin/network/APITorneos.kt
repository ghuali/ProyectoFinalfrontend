import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*
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
    val url = "http://127.0.0.1:5000/torneo/$torneoId/unirse"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $token")
                }
                // Si el backend no necesita body, puedes omitir setBody
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
