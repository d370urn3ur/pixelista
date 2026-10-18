package the.autarch.pixelista.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import the.autarch.pixelista.MainActivity
import the.autarch.pixelista.data.PixelArtRepository

private val WIDGET_INDEX_KEY = intPreferencesKey("widget_index")

class PixelistaAppWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val storedIndex = prefs[WIDGET_INDEX_KEY] ?: 0
            WidgetContent(storedIndex)
        }
    }

    @SuppressLint("InlinedApi")
    @Composable
    fun WidgetContent(storedIndex: Int) {

        val context = LocalContext.current
        val repository = remember(context) { PixelArtRepository(context) }

        val size = LocalSize.current
        val density = context.resources.displayMetrics.density
        val targetWidthPx = (size.width.value * density).toInt().coerceAtLeast(1)
        val targetHeightPx = (size.height.value * density).toInt().coerceAtLeast(1)

        val files = remember(storedIndex) {
            repository.reloadDb()
            repository.images.value
        }

        val keys = remember(files) {
            files.entries.sortedBy { it.key }.map { it.key }
        }

        val safeIndex = if (keys.isNotEmpty()) storedIndex.coerceIn(0, keys.size - 1) else 0
        val selectedKey = keys.getOrNull(safeIndex)
        val imageData = selectedKey?.let { key -> files[key] }

        val bitmap = remember(imageData, targetWidthPx, targetHeightPx) {
            imageData?.let { data ->
                val rows = data.size
                val cols = data.firstOrNull()?.size ?: 0
                if (rows > 0 && cols > 0 && targetWidthPx > 0 && targetHeightPx > 0) {
                    val bitmap = createBitmap(targetWidthPx, targetHeightPx)
                    val canvas = Canvas(bitmap)
                    val paint = Paint().apply {
                        isAntiAlias = false
                    }

                    val widthF = targetWidthPx.toFloat()
                    val heightF = targetHeightPx.toFloat()

                    val viewportLeft: Float
                    val viewportTop: Float
                    val viewportWidth: Float
                    val viewportHeight: Float

                    if (rows >= cols) {
                        val multiplier = cols.toFloat() / rows.toFloat()
                        viewportHeight = heightF
                        viewportWidth = heightF * multiplier
                        viewportLeft = (widthF - viewportWidth) / 2f
                        viewportTop = (heightF - viewportHeight) / 2f
                    } else {
                        val multiplier = rows.toFloat() / cols.toFloat()
                        viewportWidth = widthF
                        viewportHeight = widthF * multiplier
                        viewportLeft = (widthF - viewportWidth) / 2f
                        viewportTop = (heightF - viewportHeight) / 2f
                    }

                    val sizeX = viewportWidth / cols.toFloat()
                    val sizeY = viewportHeight / rows.toFloat()

                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            val currX = viewportLeft + col * sizeX
                            val currY = viewportTop + row * sizeY
                            paint.color = data[row][col].toArgb()
                            canvas.drawRect(
                                currX,
                                currY,
                                currX + sizeX,
                                currY + sizeY,
                                paint
                            )
                        }
                    }
                    bitmap
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
                        contentDescription = selectedKey
                    )
                }
            }

            Row(
                GlanceModifier.fillMaxSize()
            ) {
                // Left tap area for previous image
                Box(
                    GlanceModifier
                        .fillMaxHeight()
                        .defaultWeight()
                        .clickable(actionRunCallback<PrevImageAction>()),
                ) {}

                // Middle spacer filling remaining width
                Spacer(
                    GlanceModifier
                        .fillMaxHeight()
                        .defaultWeight()
                )

                // Right tap area for next image
                Box(
                    GlanceModifier
                        .fillMaxHeight()
                        .defaultWeight()
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

        updateAppWidgetState(context, glanceId) { prefs ->
            val currentIndex = prefs[WIDGET_INDEX_KEY] ?: 0
            val maxIndex = if (keys.isNotEmpty()) keys.size - 1 else 0
            val newIndex = if (currentIndex > 0) currentIndex - 1 else maxIndex
            prefs[WIDGET_INDEX_KEY] = newIndex
        }
        PixelistaAppWidget().update(context, glanceId)
    }
}

class NextImageAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val repository = PixelArtRepository(context)
        repository.reloadDb()
        val files = repository.images.value
        val keys = files.entries.sortedBy { it.key }.map { it.key }

        updateAppWidgetState(context, glanceId) { prefs ->
            val currentIndex = prefs[WIDGET_INDEX_KEY] ?: 0
            val maxIndex = if (keys.isNotEmpty()) keys.size - 1 else 0
            val newIndex = if (currentIndex < maxIndex) currentIndex + 1 else 0
            prefs[WIDGET_INDEX_KEY] = newIndex
        }
        PixelistaAppWidget().update(context, glanceId)
    }
}
