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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import model.Juego
import model.JugadorResumen
import model.User
import network.apiLogIn
import network.apiRegister
import network.getJuegosIndividuales
import network.getJugadoresPorJuego

class PlayerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }
        var authMessage by remember { mutableStateOf<String?>(null) }
        var loggedUser by remember { mutableStateOf<User?>(null) }

        var juegos by remember { mutableStateOf<List<Juego>>(emptyList()) }
        var selectedGameIndex by remember { mutableStateOf(0) }
        val selectedGame = juegos.getOrNull(selectedGameIndex)
        var jugadoresPorJuego by remember { mutableStateOf<Map<Int, List<JugadorResumen>>>(emptyMap()) }

        LaunchedEffect(Unit) {
            getJuegosIndividuales { juegosCargados ->
                juegos = juegosCargados
            }
        }

        LaunchedEffect(juegos) {
            if (juegos.isNotEmpty()) {
                selectedGameIndex = 0
                selectedGame?.let { juego ->
                    getJugadoresPorJuego(juego.id_juego) { jugadores ->
                        jugadoresPorJuego = mapOf(juego.id_juego to jugadores)
                    }
                }
            }
        }

        LaunchedEffect(selectedGameIndex) {
            selectedGame?.let { juego ->
                getJugadoresPorJuego(juego.id_juego) { jugadores ->
                    jugadoresPorJuego = mapOf(juego.id_juego to jugadores)
                }
            }
        }

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFF00))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (selectedGameIndex > 0) selectedGameIndex--
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Anterior")
                    }

                    Box(
                        modifier = Modifier
                            .border(2.dp, Color.Black)
                            .background(Color(0xFFFFCC80))
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = selectedGame?.nombre ?: "No hay juegos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    IconButton(onClick = {
                        if (selectedGameIndex < juegos.size - 1) selectedGameIndex++
                    }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Siguiente")
                    }
                }

// Título de juego
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedGame?.nombre ?: "",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Encabezado tabla
                TableHeader()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    val jugadores = selectedGame?.let { jugadoresPorJuego[it.id_juego] } ?: emptyList()
                    val totalFilas = 14
                    val jugadoresRellenados = jugadores + List(totalFilas - jugadores.size) {
                        JugadorResumen("-", 0, 0)
                    }

                    itemsIndexed(jugadoresRellenados) { index, jugador ->
                        TableRow(jugador.nombre, jugador.victorias.toString(), jugador.derrotas.toString(), index)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje de autenticación
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

            // Botón volver
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

        // Diálogos
        if (showSignInDialog) {
            SignInDialog(
                onDismiss = { showSignInDialog = false },
                onSuccess = { user ->
                    showSignInDialog = false
                    println("Login exitoso. Usuario: ${user.nombre}")
                }
            )
        }

        if (showSignUpDialog) {
            SignUpDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    showSignUpDialog = false
                    println("Registro exitoso. Usuario: ${user.nombre}")
                }
            )
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

    @Composable
    fun SignUpDialog(
        onDismiss: () -> Unit,
        onSignUpSuccess: (User) -> Unit
    ) {
        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Registrarse",
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
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            apiRegister(nombre, email, password,
                                onSuccessResponse = { user ->
                                    onSignUpSuccess(user)
                                    onDismiss()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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
    fun SignInDialog(
        onDismiss: () -> Unit,
        onSuccess: (User) -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
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
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            apiLogIn(
                                email, password,
                                onSuccessResponse = { user ->
                                    onSuccess(user)
                                    onDismiss()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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
}
