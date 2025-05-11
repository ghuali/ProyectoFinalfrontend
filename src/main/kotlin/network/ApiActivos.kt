package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Proyecto
import network.NetworkUtils.httpClient

fun getProyectosActivos(onSuccessResponse: (List<Proyecto> ) -> Unit)  {
    val url = "http://127.0.0.1:5000/proyecto/proyectos_activos"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val respuesta = httpClient.get(url)
            val responseBody = respuesta.bodyAsText()
            if (respuesta.status == HttpStatusCode.OK) {
                val proyectos = respuesta.body<List<Proyecto>>()
                onSuccessResponse(proyectos)
            } else {
                print("Error: ${respuesta.status}, Body: ${responseBody}")
            }
        } catch (e: Exception) {
            print("Exception: ${e.message}")
        }
    }
}