package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.User
import network.*

class EditScreen(): Screen{

    @Composable
    override fun Content(){
        val usuario = SessionManager.currentUser
        println("Token en EditScreen: ${SessionManager.authToken}")

        editarScreen(
            usuario = usuario,
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
                SessionManager.clearSession()
            }
        )
    }

    @Composable
    fun editarScreen(
        usuario: User?,
        onLogout: () -> Unit
    ){
        val navigator = LocalNavigator.current

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEB3B))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    ) {
                        Image(
                            painter = painterResource("CanaryEsportsImg.png"),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "CANARY'S ESPORTS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Row {
                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Text(
                                currentUser?.nombre ?: "Usuario",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { expanded = true }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                if (usuario?.rol == "administrador") {
                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        navigator?.push(AdminScreen())  // O la pantalla que administre
                                    }) {
                                        Text("Administrar")
                                    }
                                }
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onLogout()
                                    navigator?.pop()
                                }) {
                                    Text("Cerrar sesión")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                usuario?.let { user ->
                    EditBloque(
                        usuario = user,
                        onUserUpdated = { updatedUser ->
                            SessionManager.currentUser = updatedUser
                            println("Usuario actualizado: $updatedUser")
                        }
                    )
                }
            }
            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F))
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
    }

    @Composable
    fun EditBloque(usuario: User, onUserUpdated: (User) -> Unit) {
        var nombre by remember { mutableStateOf(usuario.nombre) }
        var email by remember { mutableStateOf(usuario.email) }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var showDialog by remember { mutableStateOf(false) }

        val navigator = LocalNavigator.current

        Box(
            modifier = Modifier
                .background(Color(0xFFD3D3D3))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Editar Usuario",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.Black
                )

                Text("Nombre", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Introducir Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                Text("Email", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Introducir Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                Text("Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Nueva Contraseña (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() && email.isBlank() && password.isBlank()) {
                            errorMessage = "Debe rellenar al menos un campo"
                            return@Button
                        }
                        errorMessage = null
                        showDialog = true  // Mostrar diálogo de confirmación
                    },
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios", color = Color.Yellow, fontWeight = FontWeight.Bold)
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Dialog de confirmación
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmar actualización") },
                    text = { Text("¿Quieres actualizar los datos del usuario?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                val token = SessionManager.authToken
                                if (token == null) {
                                    errorMessage = "No hay token disponible. Debes iniciar sesión."
                                    return@TextButton
                                }

                                apiEditUser(
                                    idUsuario = usuario.id,
                                    nombre = nombre.takeIf { it.isNotBlank() },
                                    email = email.takeIf { it.isNotBlank() },
                                    password = password.takeIf { it.isNotBlank() },
                                    token = token,
                                    onSuccessResponse = {
                                        errorMessage = null
                                        onUserUpdated(it)
                                        navigator?.pop()  // Volver a pantalla anterior
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}


