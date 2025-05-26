package the.autarch.pixelista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import the.autarch.pixelista.ui.theme.PixelistaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixelistaTheme {
        App()
    }
}