//package network
//import model.LoginRequest
//import network.NetworkUtils.httpClient
//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import utils.sha512
//
//
//fun apiLogIn(usuario: String, password: String, onSucessResponse: (User) -> Unit) {
//    val url = "http://127.0.0.1:5000/gestor/login"
//    CoroutineScope(Dispatchers.IO).launch {
//        val response = httpClient.post(url){
//            contentType(ContentType.Application.Json)
//            setBody(LoginRequest(usuario, sha512(password)))
//        }
//
//    if (response.status == HttpStatusCode.OK) {
//        val user = response.body<User>()
//        onSucessResponse(user)
//    } else {
//        println("Error: ${response.status}, Body: ${response.bodyAsText()}")
//    }
//}
//}
