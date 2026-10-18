package the.autarch.pixelista

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import the.autarch.pixelista.ui.theme.PixelistaTheme

class MainActivity: ComponentActivity() {

    private val selectedImageKey = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        selectedImageKey.value = intent?.getStringExtra("selected_image_key")

        setContent {
            ThemedApp(selectedImageKey = selectedImageKey.value)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        selectedImageKey.value = intent.getStringExtra("selected_image_key")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixelistaTheme {
        App()
    }
}
