import androidx.compose.ui.window.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import Screen.StartScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Canary's Esports",
        state = WindowState(
            placement = WindowPlacement.Maximized, // Abre maximizada
            size = DpSize.Unspecified
        ),
        undecorated = false,
        resizable = true,
    ) {
        App()
    }
}

@Composable
fun App() {
    Navigator(screen = StartScreen())
}
