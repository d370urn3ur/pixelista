package the.autarch.pixelista.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.width
import the.autarch.pixelista.MainActivity
import the.autarch.pixelista.data.PixelArtRepository
import the.autarch.pixelista.data.StoragePrefs

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
        val repository = remember(context) {
            PixelArtRepository(context).apply { reloadDb() }
        }
        val files by repository.images.collectAsState(initial = emptyMap())

        val keys = remember(files) {
            files.entries.sortedBy { it.key }.map { it.key }
        }

        val storedIndex = StoragePrefs.getWidgetIndex(context)
        val safeIndex = if (keys.isNotEmpty()) storedIndex.coerceIn(0, keys.size - 1) else 0
        val selectedKey = keys.getOrNull(safeIndex)
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

            // Center image area (filling remaining space, clickable to open MainActivity)
            Box(
                GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
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

            Row(
                GlanceModifier.fillMaxSize()
            ) {
                // Left tap area for previous image (Red background for visualization)
                Box(
                    GlanceModifier
                        .fillMaxHeight()
                        .width(72.dp)
                        .background(Color.Red)
                        .clickable(actionRunCallback<PrevImageAction>()),
                ) {}

                // Middle spacer filling remaining width between Red and Blue
                Spacer(
                    GlanceModifier
                        .fillMaxHeight()
                        .defaultWeight()
                )

                // Right tap area for next image (Blue background for visualization)
                Box(
                    GlanceModifier
                        .fillMaxHeight()
                        .width(72.dp)
                        .background(Color.Blue)
                        .clickable(actionRunCallback<NextImageAction>()),
                ) {}
            }
        }
    }
}

class PrevImageAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val repository = PixelArtRepository(context)
        repository.reloadDb()
        val files = repository.images.value
        val keys = files.entries.sortedBy { it.key }.map { it.key }
        if (keys.isNotEmpty()) {
            val currentIndex = StoragePrefs.getWidgetIndex(context)
            val newIndex = if (currentIndex > 0) currentIndex - 1 else keys.size - 1
            StoragePrefs.setWidgetIndex(context, newIndex)
            PixelistaAppWidget().update(context, glanceId)
        }
    }
}

class NextImageAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val repository = PixelArtRepository(context)
        repository.reloadDb()
        val files = repository.images.value
        val keys = files.entries.sortedBy { it.key }.map { it.key }
        if (keys.isNotEmpty()) {
            val currentIndex = StoragePrefs.getWidgetIndex(context)
            val newIndex = if (currentIndex < keys.size - 1) currentIndex + 1 else 0
            StoragePrefs.setWidgetIndex(context, newIndex)
            PixelistaAppWidget().update(context, glanceId)
        }
    }
}
