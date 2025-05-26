package the.autarch.pixelista

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.floor
import kotlin.math.roundToInt

object ScreenObj {

    @Composable
    fun Screen(
        pxField: List<List<Color>>,
        modifier: Modifier = Modifier,
        drawGuides: Boolean = false,
        onChangePixel: (Int, Int) -> Unit
    ) {

        val rows = pxField.size
        val cols = pxField.firstOrNull()?.size ?: 0
        var cSize by remember { mutableStateOf(Size.Zero) }
        var lastTapped by remember { mutableStateOf(Offset.Zero) }

        Canvas(
            modifier.pointerInput(rows, cols) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    lastTapped += dragAmount
                    val pxRect = calculatePxOrigin(cSize, rows, cols)
                    val tile = lookupTile(pxRect, lastTapped)
                    if (tile.first in 0..<rows && tile.second in 0..<cols) {
                        onChangePixel(tile.second, tile.first)
                    }
                }
            }
            .pointerInput(rows, cols) {
                detectTapGestures(
                    onPress = { offset ->
                        val pxRect = calculatePxOrigin(cSize, rows, cols)
                        val tile = lookupTile(pxRect, offset)
                        if (tile.first in 0..<cols && tile.second in 0..<rows) {
                            onChangePixel(tile.second, tile.first)
                        }
                        lastTapped = offset
                    },
                    onTap = { offset ->
                        Log.i("TAP", "Tap at $offset")
                    }
                )
            }
        ) {

            cSize = size
            val pxRect = calculatePxOrigin(size, rows, cols)

            for (row in 0..<rows) {
                for (col in 0..<cols) {
                    val currX = pxRect.left + col * pxRect.width
                    val currY = pxRect.top + row * pxRect.height
                    val color = pxField[row][col]
                    drawRect(
                        color,
                        topLeft = Offset(currX, currY),
                        size = Size(pxRect.width, pxRect.height)
                    )
                }
            }

            if (drawGuides) {
                val guideColor = Color(0xffff5733)
                for (row in 0..<rows) {
                    val currY = pxRect.top + row * pxRect.width
                    drawLine(
                        guideColor,
                        start = Offset(pxRect.left, currY),
                        end = Offset(pxRect.left + cols * pxRect.width, currY),
                        strokeWidth = 2f
                    )
                }
                for (col in 0..<cols) {
                    val currX = pxRect.left + col * pxRect.width
                    drawLine(
                        guideColor,
                        start = Offset(currX, pxRect.top),
                        end = Offset(currX, pxRect.top + rows * pxRect.height),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }

    private fun calculatePxOrigin(containerSize: Size, rows: Int, cols: Int): Rect {

        val size: Float
        var offsetX = 0f
        var offsetY = 0f

        if (containerSize.height >= containerSize.width) {
            // Portrait canvas
            val containerAspect = containerSize.height / containerSize.width
            val fieldAspect = rows.toFloat() / cols.toFloat()

            if (fieldAspect < containerAspect) {
                size = containerSize.width / cols
                offsetY = (containerSize.height - rows * size) / 2
            } else {
                size = containerSize.height / rows
                offsetX = (containerSize.width - cols * size) / 2
            }
        } else {
            // Landscape canvas
            val containerAspect = containerSize.width / containerSize.height
            val fieldAspect = cols.toFloat() / rows.toFloat()

            if (fieldAspect < containerAspect) {
                size = containerSize.height / rows
                offsetX = (containerSize.width - cols * size) / 2
            } else {
                size = containerSize.width / cols
                offsetY = (containerSize.height - rows * size) / 2
            }
        }

        return Rect(Offset(offsetX, offsetY), Size(size, size))
    }

    private fun lookupTile(pxRect: Rect, offset: Offset): Pair<Int, Int> {
        val xOffset = offset.x - pxRect.left
        val yOffset = offset.y - pxRect.top
        val tileX = floor(xOffset / pxRect.width).roundToInt()
        val tileY = floor(yOffset / pxRect.height).roundToInt()
        return tileX to tileY
    }
}
