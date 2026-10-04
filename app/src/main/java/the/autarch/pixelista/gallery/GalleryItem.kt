package the.autarch.pixelista.gallery

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GalleryItem(data: List<List<Color>>, selected: Boolean, onSelect: () -> Unit) {

    val selectedMod = if (selected) {
        Modifier.border(width = 5.dp, color = Color.Magenta)
    } else {
        Modifier.Companion
    }

    val rows = data.size
    val cols = data.firstOrNull()?.size ?: 0

    Canvas(selectedMod.size(100.dp).clickable { onSelect() }) {

        var viewport = Rect.Zero

        if (rows >= cols) {
            // portrait / square
            val multiplier = cols.toFloat() / rows.toFloat()
            val height = size.height
            val width = height * multiplier
            val x = (size.width - width) / 2f
            val y = (size.height - height) / 2f
            viewport = viewport.copy(left = x, top = y, right = x + width, bottom = y + height)
        } else {
            // landscape
            val multiplier = rows.toFloat() / cols.toFloat()
            val width = size.width
            val height = width * multiplier
            val x = (size.width - width) / 2f
            val y = (size.height - height) / 2f
            viewport = viewport.copy(left = x, top = y, right = x + width, bottom = y + height)
        }

        val sizeX = viewport.width / cols.toFloat()
        val sizeY = viewport.height / rows.toFloat()
        for (row in 0..<rows) {
            for (col in 0..<cols) {
                val currX = viewport.left + col * sizeX
                val currY = viewport.top + row * sizeY
                drawRect(data[row][col], Offset(currX, currY), Size(sizeX, sizeY))
            }
        }
    }
}