package the.autarch.pixelista

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO: Tutorial
// TODO: share image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val context = LocalContext.current
    var filename by remember { mutableStateOf(Uuid.random().toHexString()) }
    var showDimensDialog by remember { mutableStateOf(false) }
    var drawGuides by remember { mutableStateOf(true) }

    var pxField by remember {
        mutableStateOf(List(8) {    // rows
            List(8) {   // columns
                Color.Black
            }
        }) }

    val rows = pxField.size
    val cols = pxField.firstOrNull()?.size ?: 0

    var brushColor by remember { mutableStateOf(Color.Black) }
    var paletteSelection by remember { mutableStateOf(PaletteSelection.default) }
    val history by remember { mutableStateOf(History()) }
    val disk by remember { mutableStateOf(Disk(context)) }
    val galleryImages by disk.images.collectAsStateWithLifecycle()
    var isGalleryOpen by remember { mutableStateOf(false) }
    var isPaletteSelectorOpen by remember { mutableStateOf(false) }
    var showNewImageDialog by remember { mutableStateOf(false) }
    var showDeleteDialogName: String? by remember { mutableStateOf(null) }

    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isHeightAtLeastMedium = sizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.OpenDocumentTree()
//    ) { uri ->
//        if (uri != null) {
//            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//            context.contentResolver.takePersistableUriPermission(uri, flags)
//            StoragePrefs.saveTreeUri(context, uri.toString())
//            asdf
//        }
//    }

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
                    Palette.ColorPalette(
                        paletteSelection,
                        brushColor,
                        mod
                    ) { selectedColor ->
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
                        onNewImage = { showNewImageDialog = true },
                        onSave = { disk.save(filename, pxField) },
                        onOpenGallery = { isGalleryOpen = true },
                        onOpenPaletteSelector = { isPaletteSelectorOpen = true }
                    )
                }
            )

            if (showDimensDialog) {

                DimensDialogWheel(
                    rows,
                    cols,
                    onCancel = { showDimensDialog = false },
                    onAccept = { (rs, cs) ->
                        pxField = List(rs) {
                            List(cs) {
                                Color.Black
                            }
                        }
                        showDimensDialog = false
                    }
                )

//                DimensDialogSlider(
//                    rows,
//                    cols,
//                    onCancel = { showDimensDialog = false },
//                    onAccept = { (rs, cs) ->
//                        pxField = List(rs) {
//                            List(cs) {
//                                Color.Black
//                            }
//                        }
//                        showDimensDialog = false
//                    }
//                )
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
                            showDeleteDialogName = name
                        }
                    )
                }
            }

            AnimatedVisibility(
                isPaletteSelectorOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    Modifier.fillMaxSize()
                        .clickable { isPaletteSelectorOpen = false }
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Spacer(Modifier.weight(0.2f))
                    PaletteSelector(
                        paletteSelection,
                        Modifier
                            .animateEnterExit(
                                enter = slideInHorizontally(initialOffsetX = { it }),
                                exit = slideOutHorizontally(targetOffsetX = { it })
                            )
                            .fillMaxHeight().weight(0.8f),
                        onSelect = { selection ->
                            paletteSelection = selection
                            isPaletteSelectorOpen = false
                        },
                    )
                }
            }

            if (showNewImageDialog) {
                VerificationDialog<Unit>(
                    title = stringResource(R.string.dialog_title_new_image),
                    message = stringResource(R.string.dialog_message_new_image),
                    onCancel = { showNewImageDialog = false },
                    onAccept = {
                        showNewImageDialog = false
                        filename = Uuid.random().toString()
                        pxField = List(rows) { List(cols) { Color.Black } }
                        history.clear()
                    }
                )
            }

            showDeleteDialogName?.let { name ->
                VerificationDialog(
                    title = stringResource(R.string.dialog_title_delete_image),
                    message = stringResource(R.string.dialog_message_delete_image),
                    payload = name,
                    onCancel = { showDeleteDialogName = null },
                    onAccept = {
                        showDeleteDialogName = null
                        disk.delete(name)
                    }
                )
            }
        }
    }
}