import androidx.compose.ui.window.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import Screen.StartScreen
import ViewModel.SessionManager
import androidx.compose.runtime.LaunchedEffect

fun main() {
    // Cargar sesión antes de mostrar la ventana
    SessionManager.loadSession()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Canary's Esports",
            state = WindowState(
                placement = WindowPlacement.Maximized,
                size = DpSize.Unspecified
            ),
            undecorated = false,
            resizable = true,
        ) {
            App()
        }
    }
}


@Composable
fun App() {
    // Carga el token guardado al iniciar la app
    LaunchedEffect(Unit) {
        SessionManager.loadSession()
    }

    Navigator(screen = StartScreen())
}

