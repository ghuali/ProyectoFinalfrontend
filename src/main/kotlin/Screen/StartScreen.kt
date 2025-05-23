package Screen

import ViewModel.SessionManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.User
import network.apiLogIn
import network.apiRegister

class StartScreen : Screen {
    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        PantallaInicio(
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
    fun PantallaInicio(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ) {
        val navigator = LocalNavigator.current
        var showLoginDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7B1FA2), Color(0xFF000000))
                    )
                )
                .padding(16.dp)
        ) {
            // Encabezado usuario o botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (usuario != null) {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(usuario.nombre, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                onLogout()
                            }) {
                                Text("Cerrar Sesión")
                            }
                        }
                    }
                } else {
                    Row {
                        Button(
                            onClick = { showLoginDialog = true },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Login", color = Color.Black)
                        }
                        Button(
                            onClick = { showSignUpDialog = true },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ) {
                            Text("Sign Up", color = Color.Black)
                        }
                    }
                }
            }


            val painter = painterResource("CanaryEsportsImg.png")

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "CANARY'S ESPORTS",
                    fontSize = 36.sp,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Donde los campeones compiten",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .fillMaxWidth(0.85f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { navigator?.push(WelcomeScreen()) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFFEB3B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Tablas Equipos", fontSize = 16.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { navigator?.push(PlayerScreen()) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFFB74D)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Tablas Individuales", fontSize = 16.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { navigator?.push(EventosScreen()) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFF9800)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Eventos", fontSize = 16.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { navigator?.push(TorneoScreen()) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFF9800)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Torneo", fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }
            }
        }

        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onLoginSuccess = { user ->
                    onLoginSuccess(user)
                    showLoginDialog = false
                }
            )
        }

        if (showSignUpDialog) {
            SignUpDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    onLoginSuccess(user)
                    showSignUpDialog = false
                }
            )
        }
    }

    @Composable
    fun LoginDialog(
        onDismiss: () -> Unit,
        onLoginSuccess: (User) -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Iniciar Sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        apiLogIn(email, password) { userOrNull ->
                            isLoading = false
                            onLoginSuccess(userOrNull)
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Cargando..." else "Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    }

    @Composable
    fun SignUpDialog(
        onDismiss: () -> Unit,
        onSignUpSuccess: (User) -> Unit
    ) {
        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Registrarse", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        apiRegister(nombre, email, password) { userOrNull ->
                            isLoading = false
                            onSignUpSuccess(userOrNull)
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Cargando..." else "Registrarse")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    }
}