package the.autarch.pixelista

import androidx.compose.runtime.Composable
import the.autarch.pixelista.ui.theme.PixelistaTheme

@Composable
fun ThemedApp() {
    PixelistaTheme {
        App()
    }
}