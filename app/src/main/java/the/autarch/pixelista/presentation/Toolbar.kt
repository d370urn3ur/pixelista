package the.autarch.pixelista.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import the.autarch.pixelista.R

@Composable
fun ToolsBar(
    showingGuides: Boolean,
    onClickUndo: () -> Unit,
    onClickToggleGuides: () -> Unit,
    onChangeFieldDimensions: () -> Unit,
    onNewImage: () -> Unit,
    onSave: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenPaletteSelector: () -> Unit
) {

    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWidthAtLeastExpanded = sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val list = ArrayList<@Composable () -> Unit>()

    list.add(@Composable { IconButton(onClickUndo) {
        Icon(painterResource(R.drawable.ic_undo), contentDescription = "")
    } })

    list.add(@Composable { IconButton(onClickToggleGuides) {
        Icon(
            if (showingGuides) painterResource(R.drawable.ic_grid_off)
            else painterResource(R.drawable.ic_grid_on),
            contentDescription = ""
        )
    } })

    list.add(@Composable { IconButton(onChangeFieldDimensions) {
        Icon(painterResource(R.drawable.ic_arrows_out), contentDescription = "")
    } })

    list.add(@Composable { IconButton(onNewImage) {
        Icon(painterResource(R.drawable.ic_new_file), contentDescription = "")
    } })

    list.add(@Composable { IconButton(onSave) {
        Icon(painterResource(R.drawable.ic_save), contentDescription = "")
    } })

    list.add(@Composable { IconButton(onOpenGallery) {
        Icon(painterResource(R.drawable.ic_gallery), contentDescription = "")
    } })

    list.add(@Composable { IconButton(onOpenPaletteSelector) {
        Icon(painterResource(R.drawable.ic_palette), contentDescription = "")
    } })

    if (isWidthAtLeastExpanded) {
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            list.forEach { it.invoke() }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            list.forEach { it.invoke() }
        }
    }
}

@Preview
@Composable
fun PreviewToolsBar() {
    Column {

        Spacer(Modifier)

        ToolsBar(
            showingGuides = true,
            onClickUndo = {},
            onClickToggleGuides = {},
            onChangeFieldDimensions = {},
            onNewImage = {},
            onSave = {},
            onOpenGallery = {},
            onOpenPaletteSelector = {},
        )
    }
}