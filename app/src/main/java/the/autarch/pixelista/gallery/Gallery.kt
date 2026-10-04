package the.autarch.pixelista.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import the.autarch.pixelista.R

@Composable
fun Gallery(
    files: Map<String, List<List<Color>>>,
    modifier: Modifier = Modifier,
    onLoad: (String) -> Unit,
    onDelete: (String) -> Unit
) {

    val keys = remember(files) {
        files.entries.sortedBy { it.key }.map { it.key }
    }
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
            Modifier.fillMaxSize().weight(1f)
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