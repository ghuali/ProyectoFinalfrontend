package ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.User

object SessionManager {
    var authToken by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<User?>(null)
}