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

class WelcomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        var showAuthDialog by remember { mutableStateOf(false) }

        val juegos = listOf("Fortnite", "COD")
        var selectedGame by remember { mutableStateOf(juegos[0]) }

        val equiposPorJuego = mapOf(
            "Fortnite" to listOf(
                Jugador("Team Alpha", "10", "3", "-"),
                Jugador("Team Beta", "8", "5", "-")
            ),
            "COD" to listOf(
                Jugador("Team Delta", "11", "4", "-"),
                Jugador("Team Omega", "6", "7", "-")
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    ) {
                        Image(
                            painter = painterResource("CanaryEsportsImg.png"),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(50.dp) // o ajusta el tamaño a lo que quede mejor
                                .padding(end = 8.dp) // separación entre logo y texto
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
                            onClick = { showAuthDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Log in", color = Color.White)
                        }

                        Button(
                            onClick = { showAuthDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text("Sign up", color = Color.White)
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
                        val currentIndex = juegos.indexOf(selectedGame)
                        if (currentIndex > 0) selectedGame = juegos[currentIndex - 1]
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Anterior")
                    }

                    Box(
                        modifier = Modifier
                            .border(2.dp, Color.Black)
                            .background(Color(0xFFFFCC80))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            juegos.forEach { juego ->
                                val isSelected = juego == selectedGame
                                Text(
                                    text = juego,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .background(if (isSelected) Color(0xFFFFCDD2) else Color.Transparent)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable { selectedGame = juego },
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    IconButton(onClick = {
                        val currentIndex = juegos.indexOf(selectedGame)
                        if (currentIndex < juegos.size - 1) selectedGame = juegos[currentIndex + 1]
                    }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Siguiente")
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

                // Tabla: cabecera
                TableHeader()

                // Tabla: filas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    val equipos = equiposPorJuego[selectedGame] ?: emptyList()
                    val totalFilas = 14
                    val equiposRellenados = equipos + List(totalFilas - equipos.size) {
                        Jugador("-", "-", "-", "-")
                    }

                    itemsIndexed(equiposRellenados) { index, equipo ->
                        TableRow(
                            nombre = equipo.nombre,
                            victorias = equipo.victorias,
                            derrotas = equipo.derrotas,
                            index = index
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Botón Volver
            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)  // Esto lo coloca en la esquina inferior derecha
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F))
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }

        // Dialogo de autenticación
        if (showAuthDialog) {
            Dialog(onDismissRequest = { showAuthDialog = false }) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFD3D3D3))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sign In",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = Color.Black
                        )

                        var username by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }

                        Text("Usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        TextField(
                            value = username,
                            onValueChange = { username = it },
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
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { /* funcionalidad pendiente */ },
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Iniciar Sesión", color = Color.Yellow, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showAuthDialog = false },
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

    @Composable
    private fun TableHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF424242))
                .border(width = 1.dp, color = Color.Black)
                .padding(vertical = 12.dp)
        ) {
            listOf("Equipo", "Victorias", "Derrotas").forEach { title ->
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
