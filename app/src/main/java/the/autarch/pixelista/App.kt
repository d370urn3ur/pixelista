package the.autarch.pixelista

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App() {

    var filename by remember { mutableStateOf(Uuid.random().toHexString()) }
    var pxRows by remember { mutableIntStateOf(8) }
    var pxCols by remember { mutableIntStateOf(8) }
    var showDimensDialog by remember { mutableStateOf(false) }
    var drawGuides by remember { mutableStateOf(true) }

    var pxField by remember(pxRows, pxCols) { mutableStateOf(List(pxRows) {
        List(pxCols) { Color.Black }
    }) }

    var brushColor by remember { mutableStateOf(Color.Black) }

    val history by remember(pxRows, pxCols) { mutableStateOf(History()) }
    val disk by remember { mutableStateOf(Disk()) }
    val galleryImages by disk.images.collectAsStateWithLifecycle()
    var isGalleryOpen by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Box(Modifier.padding(innerPadding)) {

            Column {

                Palette.ColorPalette(brushColor, Modifier.weight(0.1f)) { selectedColor ->
                    brushColor = selectedColor
                }

                ScreenObj.Screen(
                    pxField,
                    Modifier.fillMaxWidth().weight(0.9f),
                    drawGuides
                ) { row, col ->
                    pxField = List(pxRows) { r ->
                        List(pxCols) { c ->
                            if (row == r && col == c) {
                                history.addOperation(Operation(row, col, pxField[r][c]))
                                brushColor
                            } else {
                                pxField[r][c]
                            }
                        }
                    }
                }

                Toolbar.Tools(
                    drawGuides,
                    Modifier.fillMaxWidth(),
                    onClickUndo = { pxField = history.undo(pxField) },
                    onClickToggleGuides = { drawGuides = !drawGuides },
                    onChangeFieldDimensions = { showDimensDialog = true },
                    onNewImage = {
                        filename = Uuid.random().toString()
                        pxField = List(pxRows) { List(pxCols) { Color.Black } }
                        history.clear()
                    },
                    onSave = { disk.save(filename, pxField) },
                    onOpenGallery = { isGalleryOpen = true }
                )
            }

            if (showDimensDialog) {
                DimensDialog(
                    pxCols,
                    pxRows,
                    onCancel = { showDimensDialog = false },
                    onAccept = {
                        pxCols = it.first
                        pxRows = it.second
                        showDimensDialog = false
                    }
                )
            }

            AnimatedVisibility(
                isGalleryOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    Modifier.fillMaxSize()
                        .clickable { isGalleryOpen = false }
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Spacer(Modifier.weight(0.2f))
                    Gallery(
                        galleryImages,
                        Modifier
                            .animateEnterExit(
                                enter = slideInHorizontally(initialOffsetX = { it }),
                                exit = slideOutHorizontally(targetOffsetX = { it })
                            )
                            .background(Color.LightGray).fillMaxHeight().weight(0.8f),
                        onLoad = { name ->
                            disk.load(name)?.let { data ->
                                pxRows = data.size
                                pxCols = data.firstOrNull()?.size ?: 0
                                pxField = data
                                history.clear()
                                filename = name
                                isGalleryOpen = false
                            }
                        }
                    )
                }
            }
        }
    }
}