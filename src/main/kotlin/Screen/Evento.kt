package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Evento
import model.User
import network.apiLogIn
import network.apiRegister
import network.getEventos
import utils.LoginDialog
import utils.RegisterDialog

class EventosScreen : Screen {
    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        eventoPantalla(
            usuario = usuario,
            onLoginSuccess = { user ->
                // Actualizamos SessionManager aquí
                SessionManager.authToken = user.token
                SessionManager.currentUser = user
            },
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
            }
        )
    }

    @Composable
    fun eventoPantalla(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ){
        val navigator = LocalNavigator.current
        val eventosState = remember { mutableStateOf<List<Evento>>(emptyList()) }

        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        // Llama al backend para obtener los eventos
        LaunchedEffect(Unit) {
            getEventos { eventos ->
                println("Eventos recibidos: $eventos")
                eventosState.value = eventos
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
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
                        if (usuario != null) {
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
                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        navigator?.push(EditScreen())
                                    }) {
                                        Text("Editar perfil")
                                    }

                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        onLogout()
                                    }) {
                                        Text("Cerrar sesión")
                                    }
                                }
                            }

                        } else {

                            Button(
                                onClick = { showSignInDialog = true },
                                colors = ButtonDefaults.buttonColors(Color.Black),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Log in", color = Color.White)
                            }

                            Button(
                                onClick = { showSignUpDialog = true },
                                colors = ButtonDefaults.buttonColors(Color.Black)
                            ) {
                                Text("Sign up", color = Color.White)
                            }
                        }
                    }
                }

                // Título
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Eventos", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Lista de eventos reales
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    items(eventosState.value) { evento ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(evento.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tipo: ${evento.tipo}", color = Color.White, fontSize = 16.sp)
                                Text("Año: ${evento.año}", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }

                }
            }

            // Botón Volver
            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F))
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
        if (showSignInDialog) {
            LoginDialog(
                onDismiss = { showSignInDialog = false },
                onLoginSuccess = { user ->
                    showSignInDialog = false
                    onLoginSuccess(user)
                    println("Login exitoso. Usuario: ${user.nombre}")
                }
            )
        }

        if (showSignUpDialog) {
            RegisterDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    showSignUpDialog = false
                    println("Registro exitoso. Usuario: ${user.nombre}")
                    onLoginSuccess(user)
                }
            )

        }
    }
}
