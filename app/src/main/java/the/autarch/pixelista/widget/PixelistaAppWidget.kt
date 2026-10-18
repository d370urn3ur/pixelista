package the.autarch.pixelista.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import the.autarch.pixelista.data.PixelArtRepository

class PixelistaAppWidget: GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @SuppressLint("InlinedApi")
    @Composable
    fun WidgetContent() {

        val context = LocalContext.current
        val repository = remember(context) { PixelArtRepository(context) }
        val files by repository.images.collectAsState(initial = emptyMap())

        val keys = remember(files) {
            files.entries.sortedBy { it.key }.map { it.key }
        }

        val selectedKey = keys.firstOrNull()
        val imageData = selectedKey?.let { key -> files[key] }

        val bitmap = remember(imageData) {
            imageData?.let { data ->
                val rows = data.size
                val cols = data.firstOrNull()?.size ?: 0
                if (rows > 0 && cols > 0) {
                    val pixels = IntArray(cols * rows)
                    var index = 0
                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            pixels[index++] = data[row][col].toArgb()
                        }
                    }
                    createBitmap(cols, rows).apply {
                        setPixels(pixels, 0, cols, 0, 0, cols, rows)
                    }
                } else {
                    null
                }
            }
        }

        Box(
            GlanceModifier
                .cornerRadius(android.R.dimen.system_app_widget_background_radius)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { bitmap ->
                Image(
                    provider = ImageProvider(bitmap),
                    modifier = GlanceModifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
            }
        }
    }
}
