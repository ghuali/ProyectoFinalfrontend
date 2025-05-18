package Screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import network.apiLogIn

class StartScreen : Screen {
    @Composable
    override fun Content() {
        var usuarioAutenticado by remember { mutableStateOf<String?>(null) }

        if (usuarioAutenticado != null) {
            PantallaConUsuario(nombre = usuarioAutenticado!!) {
                usuarioAutenticado = null
            }
        } else {
            PantallaInicio(
                onLoginSuccess = { usuario ->
                    usuarioAutenticado = usuario
                }
            )
        }
    }

    @Composable
    fun PantallaInicio(onLoginSuccess: (String) -> Unit) {
        val navigator = LocalNavigator.current
        var showLoginDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B1FA2), Color(0xFF000000))
                ))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = painterResource("CanaryEsportsImg.png")

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

                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showLoginDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        ) {
                            Text("Login", color = Color.Black)
                        }

                        Button(
                            onClick = { showSignUpDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        ) {
                            Text("Sign Up", color = Color.Black)
                        }
                    }
                }
            }
        }

        if (showLoginDialog) {
            LoginOrSignUpDialog(
                title = "Iniciar Sesión",
                onDismiss = { showLoginDialog = false },
                onLoginSuccess = onLoginSuccess
            )
        }

        if (showSignUpDialog) {
            LoginOrSignUpDialog(
                title = "Registrarse",
                onDismiss = { showSignUpDialog = false },
                onLoginSuccess = onLoginSuccess
            )
        }
    }

    @Composable
    fun LoginOrSignUpDialog(
        title: String,
        onDismiss: () -> Unit,
        onLoginSuccess: (String) -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            },
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    apiLogIn(email, password) { user ->
                        println("Usuario autenticado: $user")
                        onLoginSuccess(user.nombre)
                    }
                    onDismiss()
                }) {
                    Text("Aceptar")
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
    fun PantallaConUsuario(nombre: String, onLogout: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido, $nombre 👋", fontSize = 28.sp, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(Color.Red)) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }
}
