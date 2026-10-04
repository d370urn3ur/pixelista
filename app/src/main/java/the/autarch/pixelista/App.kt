package the.autarch.pixelista

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import the.autarch.pixelista.data.PixelArtRepository
import the.autarch.pixelista.data.StoragePrefs
import the.autarch.pixelista.dialog.DimensDialogWheel
import the.autarch.pixelista.dialog.StorageDialog
import the.autarch.pixelista.dialog.VerificationDialog
import the.autarch.pixelista.domain.DeleteDrawingUseCase
import the.autarch.pixelista.domain.LoadDrawingUseCase
import the.autarch.pixelista.domain.SaveDrawingUseCase
import the.autarch.pixelista.gallery.Gallery
import the.autarch.pixelista.paletteselection.PaletteSelector
import the.autarch.pixelista.presentation.Palette
import the.autarch.pixelista.presentation.ScreenContainer
import the.autarch.pixelista.presentation.ScreenObj
import the.autarch.pixelista.presentation.ToolsBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val context = LocalContext.current

    val viewModel: PixelArtViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = PixelArtRepository(context)
                return PixelArtViewModel(
                    repository = repository,
                    saveDrawingUseCase = SaveDrawingUseCase(repository),
                    loadDrawingUseCase = LoadDrawingUseCase(repository),
                    deleteDrawingUseCase = DeleteDrawingUseCase(repository)
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val rows = state.pxField.size
    val cols = state.pxField.firstOrNull()?.size ?: 0

    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isHeightAtLeastMedium = sizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flags)
            StoragePrefs.saveTreeUri(context, uri.toString())
            viewModel.onEvent(PixelArtUiEvent.StoragePermissionGranted(uri.toString()))
        }
    }

    LaunchedEffect(Unit) {
        if (StoragePrefs.getTreeUri(context) == null) {
            viewModel.onEvent(PixelArtUiEvent.ShowStorageDialog(true))
        }
    }

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
                        state.paletteSelection,
                        state.brushColor,
                        mod
                    ) { selectedColor ->
                        viewModel.onEvent(PixelArtUiEvent.BrushSelected(selectedColor))
                    }
                },
                screen = { mod ->
                    ScreenObj.Screen(
                        state.pxField,
                        mod,
                        state.drawGuides
                    ) { row, col ->
                        viewModel.onEvent(PixelArtUiEvent.PixelClicked(row, col))
                    }
                },
                toolbar = {
                    ToolsBar(
                        state.drawGuides,
                        onClickUndo = { viewModel.onEvent(PixelArtUiEvent.UndoClicked) },
                        onClickToggleGuides = { viewModel.onEvent(PixelArtUiEvent.ToggleGuidesClicked) },
                        onChangeFieldDimensions = {
                            viewModel.onEvent(
                                PixelArtUiEvent.ShowDimensDialog(
                                    true
                                )
                            )
                        },
                        onNewImage = { viewModel.onEvent(PixelArtUiEvent.NewImageClicked) },
                        onSave = {
                            if (StoragePrefs.getTreeUri(context) == null) {
                                viewModel.setPendingSave(state.filename, state.pxField)
                                launcher.launch(null)
                            } else {
                                viewModel.onEvent(PixelArtUiEvent.SaveClicked)
                            }
                        },
                        onOpenGallery = { viewModel.onEvent(PixelArtUiEvent.ShowGallery(true)) },
                        onOpenPaletteSelector = {
                            viewModel.onEvent(
                                PixelArtUiEvent.ShowPaletteSelector(
                                    true
                                )
                            )
                        }
                    )
                }
            )

            if (state.showDimensDialog) {
                DimensDialogWheel(
                    rows,
                    cols,
                    onCancel = { viewModel.onEvent(PixelArtUiEvent.ShowDimensDialog(false)) },
                    onAccept = { (rs, cs) ->
                        viewModel.onEvent(PixelArtUiEvent.ResizeGrid(rs, cs))
                    }
                )
            }

            AnimatedVisibility(
                state.isGalleryOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    Modifier.fillMaxSize()
                        .clickable { viewModel.onEvent(PixelArtUiEvent.ShowGallery(false)) }
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Spacer(Modifier.weight(0.2f))
                    Gallery(
                        state.galleryImages,
                        Modifier
                            .animateEnterExit(
                                enter = slideInHorizontally(initialOffsetX = { it }),
                                exit = slideOutHorizontally(targetOffsetX = { it })
                            )
                            .fillMaxHeight().weight(0.8f),
                        onLoad = { name ->
                            viewModel.onEvent(PixelArtUiEvent.LoadDrawing(name))
                        },
                        onDelete = { name ->
                            viewModel.onEvent(PixelArtUiEvent.RequestDeleteDrawing(name))
                        }
                    )
                }
            }

            AnimatedVisibility(
                state.isPaletteSelectorOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    Modifier.fillMaxSize()
                        .clickable { viewModel.onEvent(PixelArtUiEvent.ShowPaletteSelector(false)) }
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Spacer(Modifier.weight(0.2f))
                    PaletteSelector(
                        state.paletteSelection,
                        Modifier
                            .animateEnterExit(
                                enter = slideInHorizontally(initialOffsetX = { it }),
                                exit = slideOutHorizontally(targetOffsetX = { it })
                            )
                            .fillMaxHeight().weight(0.8f),
                        onSelect = { selection ->
                            viewModel.onEvent(PixelArtUiEvent.PaletteSelected(selection))
                            viewModel.onEvent(PixelArtUiEvent.ShowPaletteSelector(false))
                        },
                    )
                }
            }

            if (state.showNewImageDialog) {
                VerificationDialog<Unit>(
                    title = stringResource(R.string.dialog_title_new_image),
                    message = stringResource(R.string.dialog_message_new_image),
                    onCancel = { viewModel.onEvent(PixelArtUiEvent.ShowNewImageDialog(false)) },
                    onAccept = {
                        viewModel.onEvent(PixelArtUiEvent.ConfirmNewImage)
                    }
                )
            }

            state.showDeleteDialogName?.let { name ->
                VerificationDialog(
                    title = stringResource(R.string.dialog_title_delete_image),
                    message = stringResource(R.string.dialog_message_delete_image),
                    payload = name,
                    onCancel = { viewModel.onEvent(PixelArtUiEvent.RequestDeleteDrawing(null)) },
                    onAccept = {
                        viewModel.onEvent(PixelArtUiEvent.ConfirmDeleteDrawing(name))
                    }
                )
            }

            if (state.showStorageDialog) {
                StorageDialog(
                    onCancel = { viewModel.onEvent(PixelArtUiEvent.ShowStorageDialog(false)) },
                    onAccept = {
                        viewModel.onEvent(PixelArtUiEvent.ShowStorageDialog(false))
                        launcher.launch(null)
                    }
                )
            }
        }
    }
}