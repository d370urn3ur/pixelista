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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO: Tutorial

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val context = LocalContext.current
    var filename by remember { mutableStateOf(Uuid.random().toHexString()) }
    var showDimensDialog by remember { mutableStateOf(false) }
    var drawGuides by remember { mutableStateOf(true) }

    var pxField by remember { mutableStateOf(List(8) {
        List(8) { Color.Black }
    }) }

    val rows = pxField.size
    val cols = pxField.firstOrNull()?.size ?: 0

    var brushColor by remember { mutableStateOf(Color.Black) }

    val history by remember { mutableStateOf(History()) }
    val disk by remember { mutableStateOf(Disk(context)) }
    val galleryImages by disk.images.collectAsStateWithLifecycle()
    var isGalleryOpen by remember { mutableStateOf(false) }

    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isHeightAtLeastMedium = sizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isHeightAtLeastMedium) {
                TopAppBar(title = {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                })
            }
        }
    ) { innerPadding ->

        Box(Modifier.padding(innerPadding)) {

            ScreenContainer(
                paletteBar = { mod ->
                    Palette.ColorPalette(brushColor, mod) { selectedColor ->
                        brushColor = selectedColor
                    }
                },
                screen = { mod ->
                    ScreenObj.Screen(
                        pxField,
                        mod,
                        drawGuides
                    ) { row, col ->
                        pxField = List(rows) { r ->
                            List(cols) { c ->
                                if (row == r && col == c) {
                                    if (pxField[r][c] != brushColor) {
                                        history.addOperation(Operation(row, col, pxField[r][c]))
                                    }
                                    brushColor
                                } else {
                                    pxField[r][c]
                                }
                            }
                        }
                    }
                },
                toolbar = {
                    ToolsBar(
                        drawGuides,
                        onClickUndo = { pxField = history.undo(pxField) },
                        onClickToggleGuides = { drawGuides = !drawGuides },
                        onChangeFieldDimensions = { showDimensDialog = true },
                        onNewImage = {
                            filename = Uuid.random().toString()
                            pxField = List(rows) { List(cols) { Color.Black } }
                            history.clear()
                        },
                        onSave = { disk.save(filename, pxField) },
                        onOpenGallery = { isGalleryOpen = true }
                    )
                }
            )

            if (showDimensDialog) {
                DimensDialog(
                    rows,
                    cols,
                    onCancel = { showDimensDialog = false },
                    onAccept = { (cs, rs) ->
                        pxField = List(rs) {
                            List(cs) {
                                Color.Black
                            }
                        }
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
                            .fillMaxHeight().weight(0.8f),
                        onLoad = { name ->
                            disk.load(name)?.let { data ->
                                pxField = data
                                history.clear()
                                filename = name
                                isGalleryOpen = false
                            }
                        },
                        onDelete = { name ->
                            disk.delete(name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContainer(
    paletteBar: @Composable (Modifier) -> Unit,
    screen: @Composable (Modifier) -> Unit,
    toolbar: @Composable () -> Unit
) {

    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWidthAtLeastExpanded = sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val modif = if (isWidthAtLeastExpanded) {
        Modifier.windowInsetsPadding(WindowInsets.displayCutout)
    } else {
        Modifier
    }

    if (isWidthAtLeastExpanded) {
        Row(modif) {
            paletteBar(Modifier.weight(0.15f))
            screen(Modifier.fillMaxHeight().weight(0.85f))
            toolbar()
        }
    } else {
        Column(modif) {
            paletteBar(Modifier.weight(0.15f))
            screen(Modifier.fillMaxWidth().weight(0.85f))
            toolbar()
        }
    }
}