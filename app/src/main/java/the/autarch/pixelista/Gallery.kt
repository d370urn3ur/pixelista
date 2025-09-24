package the.autarch.pixelista

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Gallery(files: Map<String, List<List<Color>>>, modifier: Modifier = Modifier, onLoad: (String) -> Unit, onDelete: (String) -> Unit) {

    val keys = files.entries.sortedBy { it.key }.map { it.key }
    var selectedItem by remember { mutableStateOf<String?>(null) }

    Column(modifier.background(MaterialTheme.colorScheme.surface)) {

        Row(Modifier.fillMaxWidth()) {

            Spacer(Modifier.weight(1f))

            IconButton({
                selectedItem?.let {
                    onLoad(it)
                    selectedItem = null
                }
            }) {
                Icon(painterResource(R.drawable.ic_file_open), contentDescription = "")
            }

            IconButton({
                selectedItem?.let {
                    onDelete(it)
                    selectedItem = null
                }
            }) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = "")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            Modifier.fillMaxSize()
        ) {

            items(keys) { key: String ->
                val data = files[key]!!
                GalleryItem(data, selectedItem == key) {
                    selectedItem = key
                }
            }
        }
    }
}

@Composable
fun GalleryItem(data: List<List<Color>>, selected: Boolean, onSelect: () -> Unit) {
    val selectedMod = if (selected) {
        Modifier.border(width = 5.dp, color = Color.Magenta)
    } else {
        Modifier
    }

    Canvas(selectedMod.size(100.dp).clickable { onSelect() }) {

        val rows = data.size
        val cols = data.firstOrNull()?.size ?: 0

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