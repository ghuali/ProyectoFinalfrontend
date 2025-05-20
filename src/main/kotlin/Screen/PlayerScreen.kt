package Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Jugador
import model.User
import network.apiLogIn
import network.apiRegister

class PlayerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        // Estado para mostrar diálogo de login o registro
        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        // Estado para mostrar mensaje de error o info
        var authMessage by remember { mutableStateOf<String?>(null) }

        // Usuario logueado (puedes usarlo para mostrar info)
        var loggedUser by remember { mutableStateOf<User?>(null) }

        val juegos = listOf("FIFA", "Clash Royale")
        var selectedGame by remember { mutableStateOf(juegos[0]) }

        val jugadoresPorJuego = mapOf(
            "FIFA" to listOf(
                Jugador("Pepe", "10", "2", "-"),
                Jugador("Luis", "7", "5", "-")
            ),
            "Clash Royale" to listOf(
                Jugador("Carlos", "9", "3", "-"),
                Jugador("Marcos", "5", "6", "-")
            )
        )

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
                        modifier = Modifier.clickable { navigator?.pop() }
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

                // Selector de juegos (igual que antes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFF00))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        val currentIndex = juegos.indexOf(selectedGame)
                        if (currentIndex > 0) {
                            selectedGame = juegos[currentIndex - 1]
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Flecha izquierda")
                    }

                    Box(
                        modifier = Modifier
                            .border(width = 2.dp, color = Color.Black)
                            .background(Color(0xFFFFCC80))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            juegos.forEach { game ->
                                val isSelected = game == selectedGame
                                Text(
                                    text = game,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .background(if (isSelected) Color(0xFFFFCDD2) else Color.Transparent)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable { selectedGame = game },
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    IconButton(onClick = {
                        val currentIndex = juegos.indexOf(selectedGame)
                        if (currentIndex < juegos.size - 1) {
                            selectedGame = juegos[currentIndex + 1]
                        }
                    }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Flecha derecha")
                    }
                }

                // Título del juego
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selectedGame, fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Cabecera de la tabla
                TableHeader()

                // Tabla de jugadores
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    val jugadores = jugadoresPorJuego[selectedGame] ?: emptyList()
                    val totalFilas = 14
                    val jugadoresRellenados = jugadores + List(totalFilas - jugadores.size) {
                        Jugador("-", "-", "-", "-")
                    }

                    itemsIndexed(jugadoresRellenados) { index, jugador ->
                        TableRow(
                            nombre = jugador.nombre,
                            victorias = jugador.victorias,
                            derrotas = jugador.derrotas,
                            index = index
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje de autenticación (error o info)
                authMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        fontSize = 16.sp
                    )
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

        // Diálogo Login
        if (showSignInDialog) {
            AuthDialog(
                title = "Sign In",
                onDismiss = { showSignInDialog = false; authMessage = null },
                onConfirm = { email, password ->
                    apiLogIn(email, password,
                        onSuccessResponse = { user ->
                            loggedUser = user
                            authMessage = "Login exitoso: ${user.nombre}"
                            showSignInDialog = false
                        },
                        onError = { errorMsg ->
                            authMessage = errorMsg
                        }
                    )
                }
            )
        }

        // Diálogo Registro
        if (showSignUpDialog) {
            RegisterDialog(
                title = "Sign Up",
                onDismiss = { showSignUpDialog = false; authMessage = null },
                onConfirm = { nombre, email, password ->
                    apiRegister(nombre, email, password,
                        onSuccessResponse = { user ->
                            loggedUser = user
                            authMessage = "Registro exitoso: ${user.nombre}"
                            showSignUpDialog = false
                        }
                    )
                }
            )
        }
    }

    @Composable
    private fun AuthDialog(
        title: String,
        onDismiss: () -> Unit,
        onConfirm: (email: String, password: String) -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
                    )

                    Text("Usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Introducir Usuario") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Text("Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Introducir Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onConfirm(email.trim(), password.trim())
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    private fun RegisterDialog(
        title: String,
        onDismiss: () -> Unit,
        onConfirm: (nombre: String, email: String, password: String) -> Unit
    ) {
        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
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
                        placeholder = { Text("Introducir Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onConfirm(nombre.trim(), email.trim(), password.trim())
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    private fun TableHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF424242))
                .border(width = 1.dp, color = Color.Black)
                .padding(vertical = 12.dp)
        ) {
            listOf("Jugador", "Victorias", "Derrotas").forEach { title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }

    @Composable
    private fun TableRow(
        nombre: String,
        victorias: String,
        derrotas: String,
        index: Int
    ) {
        val backgroundColor = if (index % 2 == 0) Color(0xFF2E2E2E) else Color(0xFF424242)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(vertical = 12.dp)
        ) {
            listOf(nombre, victorias, derrotas).forEach { content ->
                Text(
                    text = content,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
