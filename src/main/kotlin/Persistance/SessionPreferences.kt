package Persistance

import java.util.prefs.Preferences

// Objeto para manejar el almacenamiento persistente del token
object SessionPreferences {
    private val prefs = Preferences.userRoot().node("com.canaryesports")

    fun saveAuthToken(token: String) {
        prefs.put("auth_token", token)
    }

    fun getAuthToken(): String? {
        return prefs.get("auth_token", null)
    }

    fun clearAuthToken() {
        prefs.remove("auth_token")
    }
}
