package ViewModel

import Persistance.SessionPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.User
import network.apiGetPerfil

object SessionManager {
    var authToken by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<User?>(null)

    // Carga el token al iniciar la app
    fun loadSession() {
        authToken = SessionPreferences.getAuthToken()
        // Si tenemos token, intentamos cargar el perfil del usuario
        authToken?.let { token ->
            cargarPerfil(token)
        }
    }

    // Guarda token al iniciar sesión
    fun saveSession() {
        authToken?.let {
            SessionPreferences.saveAuthToken(it)
        }
    }

    // Limpia la sesión al cerrar sesión
    fun clearSession() {
        authToken = null
        currentUser = null
        SessionPreferences.clearAuthToken()
    }

    // Nueva función para cargar el perfil de usuario desde el backend
    fun cargarPerfil(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            apiGetPerfil(token,
                onSuccess = { user ->
                    // Actualizar currentUser en el hilo principal
                    CoroutineScope(Dispatchers.Main).launch {
                        currentUser = user
                    }
                },
                onError = { errorMsg ->
                    println("Error al cargar perfil: $errorMsg")
                    // En caso de error, limpiar sesión para evitar estados inconsistentes
                    clearSession()
                }
            )
        }
    }
}
