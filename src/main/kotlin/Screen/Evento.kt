package Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class EventosScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        // Lista de torneos
        val torneos = listOf(
            Triple("Winter Cup", "12/01/2025", "Tenerife"),
            Triple("Spring Clash", "25/03/2025", "Gran Canaria"),
            Triple("Summer Brawl", "10/07/2025", "Lanzarote")
        )

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
                Text(
                    text = "CANARY'S ESPORTS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(onClick = { navigator?.pop() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            }

            // Título Torneos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Torneos", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Lista de torneos como tarjetas (no tabla)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f, fill = true)
            ) {
                items(torneos) { torneo ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(torneo.first, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Fecha: ${torneo.second}", color = Color.White, fontSize = 16.sp)
                            Text("Ubicación: ${torneo.third}", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F))
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}
