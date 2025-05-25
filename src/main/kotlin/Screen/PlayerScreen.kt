package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Juego
import model.JugadorResumen
import model.JugadorTabla
import model.User
import network.apiLogIn
import network.apiRegister
import network.getJuegosIndividuales
import network.getJugadoresPorJuego
import network.salirJuegoIndividual
import network.unirseJuegoIndividual
import utils.LoginDialog
import utils.RegisterDialog

class PlayerScreen : Screen {

    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        playerPantalla(
            usuario = usuario,
            onLoginSuccess = { user ->
                println("Asignando token: ${user.token}")
                // Actualizamos SessionManager aquí
                SessionManager.authToken = user.token
                SessionManager.currentUser = user
                SessionManager.saveSession()
            },
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
                SessionManager.clearSession()
            }
        )
    }

    @Composable
    fun playerPantalla(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ){
        val navigator = LocalNavigator.current

        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }
        var authMessage by remember { mutableStateOf<String?>(null) }

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
                                        println("Token antes de abrir EditScreen: ${SessionManager.authToken}")
                                        navigator?.push(EditScreen())
                                    }) {
                                        Text("Editar perfil")
                                    }

                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        navigator?.push(EquipoScreen())
                                    }) {
                                        Text("Equipo")
                                    }

                                    if (usuario.rol == "administrador") {
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

                // Selector de juegos
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título del juego a la izquierda
                    Text(
                        text = selectedGame?.nombre ?: "",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // Botones a la derecha
                    Row {
                        Button(
                            onClick = {
                                selectedGame?.id_juego?.let { idJuego ->
                                    val token = SessionManager.authToken
                                    if (token != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                unirseJuegoIndividual(idJuego, token) {
                                                    println("Te has unido al juego individual")
                                                    getJugadoresPorJuego(idJuego) { jugadores ->
                                                        jugadoresPorJuego = mapOf(idJuego to jugadores)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                println("Error al unirse: ${e.message}")
                                            }
                                        }
                                    } else {
                                        println("No hay token, usuario no autenticado")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Unirse")
                        }
                        Button(
                            onClick = {
                                selectedGame?.id_juego?.let { idJuego ->
                                    val token = SessionManager.authToken
                                    if (token != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                salirJuegoIndividual(idJuego, token) {
                                                    println("Has salido del juego individual")
                                                    getJugadoresPorJuego(idJuego) { jugadores ->
                                                        jugadoresPorJuego = mapOf(idJuego to jugadores)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                println("Error al salir: ${e.message}")
                                            }
                                        }
                                    } else {
                                        println("No hay token, usuario no autenticado")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                        ) {
                            Text("Salir")
                        }
                    }
                }

                // Encabezado tabla
                TableHeader()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    val jugadoresResumen = selectedGame?.let { jugadoresPorJuego[it.id_juego] } ?: emptyList()

                    val jugadoresTabla = jugadoresResumen.map {
                        JugadorTabla(
                            nombre = it.nombre,
                            victorias = it.victorias ?: 0,
                            derrotas = it.derrotas ?: 0
                        )
                    }

                    val totalFilas = 14
                    val jugadoresRellenados = jugadoresTabla + List(totalFilas - jugadoresTabla.size) {
                        JugadorTabla("-", 0, 0)
                    }

                    itemsIndexed(jugadoresRellenados) { index, jugador ->
                        TableRow(jugador.nombre, jugador.victorias.toString(), jugador.derrotas.toString(), index)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                    onLoginSuccess(user)
                    println("Registro exitoso. Usuario: ${user.nombre}")

                    // Guardar en SessionManager
                    SessionManager.authToken = user.token
                    SessionManager.currentUser = user
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
}