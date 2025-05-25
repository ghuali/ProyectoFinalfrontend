package network

import ViewModel.SessionManager
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.EquipoConCodigo
import model.EquipoResumen
import model.Usuario
import network.NetworkUtils.httpClient



fun apiObtenerEquipoDelUsuario(
    onSuccess: (EquipoConCodigo?) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/usuario"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url) {
                bearerAuth(SessionManager.authToken ?: "")
            }

            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.OK) {
                val equipo = Json.decodeFromString<EquipoConCodigo>(responseText)
                onSuccess(equipo)
            } else if (response.status == HttpStatusCode.NoContent) {
                // Usuario no tiene equipo
                onSuccess(null)
            } else {
                val jsonElement = Json.parseToJsonElement(responseText)
                val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Error desconocido"
                onError(msg)
            }
        } catch (e: Exception) {
            onError("Error al obtener equipo del usuario: ${e.message}")
        }
    }
}

fun apiCrearEquipo(
    nombre: String,
    onSuccess: (EquipoConCodigo) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/crear"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                bearerAuth(SessionManager.authToken ?: "")
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(mapOf("nombre" to nombre)))
            }

            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.Created) {
                val equipo = Json.decodeFromString<EquipoConCodigo>(responseText)
                onSuccess(equipo)
            } else {
                val jsonElement = Json.parseToJsonElement(responseText)
                val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Error desconocido"
                onError(msg)
            }
        } catch (e: Exception) {
            onError("Error al crear equipo: ${e.message}")
        }
    }
}

fun apiSalirDelEquipo(
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/salirse"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                bearerAuth(SessionManager.authToken ?: "")
                contentType(ContentType.Application.Json)
                // No body necesario para este endpoint
            }

            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.OK) {
                // El servidor responde con un mensaje en JSON, por ejemplo {"mensaje": "..."}
                val jsonElement = Json.parseToJsonElement(responseText)
                val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Operación exitosa"
                onSuccess(msg)
            } else {
                val jsonElement = Json.parseToJsonElement(responseText)
                val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Error desconocido"
                onError(msg)
            }
        } catch (e: Exception) {
            onError("Error al salir del equipo: ${e.message}")
        }
    }
}

fun apiObtenerMiembrosEquipo(
    idEquipo: Int,
    onSuccess: (List<Usuario>) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/$idEquipo/miembros"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url) {
                bearerAuth(SessionManager.authToken ?: "")
            }
            if (response.status == HttpStatusCode.OK) {
                val miembros = Json.decodeFromString<List<Usuario>>(response.bodyAsText())
                onSuccess(miembros)
            } else {
                onError("Error al obtener miembros")
            }
        } catch (e: Exception) {
            onError("Error al obtener miembros: ${e.message}")
        }
    }
}

fun apiObtenerEquipoPorCodigo(
    codigo: String,
    onSuccess: (EquipoConCodigo) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/codigo/$codigo"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url) {
                bearerAuth(SessionManager.authToken ?: "")
            }

            val responseText = response.bodyAsText()
            val jsonElement = Json.parseToJsonElement(responseText)

            if (response.status == HttpStatusCode.OK) {
                // Si tiene "mensaje" es un error, si no, parseamos a EquipoConCodigo
                if (jsonElement.jsonObject.containsKey("mensaje")) {
                    val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Error desconocido"
                    onError(msg)
                } else {
                    val equipo = Json.decodeFromJsonElement<EquipoConCodigo>(jsonElement)
                    onSuccess(equipo)
                }
            } else {
                val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Error desconocido"
                onError(msg)
            }
        } catch (e: Exception) {
            onError("Error al obtener equipo: ${e.message}")
        }
    }
}

fun apiUnirseAEquipoPorCodigo(codigo: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
    val url = "http://127.0.0.1:5000/equipo/unirse/$codigo"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                bearerAuth(SessionManager.authToken ?: "")
            }

            if (response.status == HttpStatusCode.OK) {
                val mensaje = response.body<JsonObject>()["mensaje"]?.jsonPrimitive?.content ?: "Unido correctamente"
                onSuccess(mensaje)
            } else {
                val error = response.bodyAsText()
                onError("Error al unirse: $error")
            }
        } catch (e: Exception) {
            onError("Excepción: ${e.message}")
        }
    }
}

fun apiObtenerEquipoPorFundador(
    idUsuario: Int,
    onSuccess: (EquipoConCodigo) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://127.0.0.1:5000/equipo/fundador/$idUsuario"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.get(url) {
                bearerAuth(SessionManager.authToken ?: "")
            }

            val responseText = response.bodyAsText()
            val jsonElement = Json.parseToJsonElement(responseText)

            if (response.status == HttpStatusCode.OK) {
                // Si contiene "mensaje", asumimos que no hay equipo
                if (jsonElement.jsonObject.containsKey("mensaje")) {
                    val msg = jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content ?: "Equipo no encontrado"
                    onError(msg)
                } else {
                    val equipo = Json.decodeFromJsonElement<EquipoConCodigo>(jsonElement)
                    onSuccess(equipo)
                }
            } else {
                val msg = jsonElement.jsonObject["error"]?.jsonPrimitive?.content
                    ?: jsonElement.jsonObject["mensaje"]?.jsonPrimitive?.content
                    ?: "Error desconocido"
                onError(msg)
            }
        } catch (e: Exception) {
            onError("Error de red: ${e.message}")
        }
    }
}


fun unirseJuegoEquipo(idJuego: Int, idEquipo: Int, token: String, onSuccessResponse: () -> Unit) {
    val url = "http://127.0.0.1:5000/unirse/juego-equipo"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("id_juego" to idJuego, "id_equipo" to idEquipo))
            }
            if (response.status == HttpStatusCode.Created) {
                onSuccessResponse()
            } else {
                println("Error unirseJuegoEquipo: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception en unirseJuegoEquipo: ${e.message}")
        }
    }
}

fun salirJuegoEquipo(idJuego: Int, idEquipo: Int, token: String, onSuccessResponse: () -> Unit) {
    val url = "http://127.0.0.1:5000/salir/juego-equipo"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = httpClient.post(url) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("id_juego" to idJuego, "id_equipo" to idEquipo))
            }
            if (response.status == HttpStatusCode.OK) {
                onSuccessResponse()
            } else {
                println("Error salirJuegoEquipo: ${response.status}, Body: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception en salirJuegoEquipo: ${e.message}")
        }
    }
}
