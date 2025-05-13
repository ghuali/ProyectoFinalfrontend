package Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.io.InputStream

class StartScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var showLoginDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        // Cargar imagen desde recursos
        val logoBitmap = remember { loadImageFromResources("CanaryEsportsImg.png") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen circular
            logoBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Logo Canary Esports",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .padding(bottom = 16.dp)
                )
            }

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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Text("Tablas Equipos", fontSize = 16.sp, color = Color.Black)
                    }

                    Button(
                        onClick = { navigator?.push(PlayerScreen()) },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFEB3B)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Text("Tablas Individuales", fontSize = 16.sp, color = Color.Black)
                    }

                    Button(
                        onClick = { navigator?.push(EventosScreen()) },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF9800)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
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

        if (showLoginDialog) {
            LoginOrSignUpDialog("Iniciar Sesión") { showLoginDialog = false }
        }

        if (showSignUpDialog) {
            LoginOrSignUpDialog("Registrarse") { showSignUpDialog = false }
        }
    }

    @Composable
    fun LoginOrSignUpDialog(title: String, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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

    // Función auxiliar para cargar la imagen desde la carpeta resources
    fun loadImageFromResources(resourceName: String): ImageBitmap? {
        return try {
            val stream: InputStream? = this::class.java.classLoader.getResourceAsStream(resourceName)
            stream?.use {
                val byteArray = it.readBytes()
                val skiaImage = makeFromEncoded(byteArray)
                skiaImage.toComposeImageBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
