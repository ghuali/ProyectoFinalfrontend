package Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class StartScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var showLoginDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CANARY'S ESPORTS",
                fontSize = 36.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFF1E1E1E))
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
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFEB3B)),
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
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text("Login", color = Color.Black)
                        }

                        Button(
                            onClick = { showSignUpDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.White),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Text("Sign Up", color = Color.Black)
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showLoginDialog) {
            LoginOrSignUpDialog(
                title = "Iniciar Sesión",
                onDismiss = { showLoginDialog = false }
            )
        }

        if (showSignUpDialog) {
            LoginOrSignUpDialog(
                title = "Registrarse",
                onDismiss = { showSignUpDialog = false }
            )
        }
    }

    @Composable
    fun LoginOrSignUpDialog(title: String, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
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
}
