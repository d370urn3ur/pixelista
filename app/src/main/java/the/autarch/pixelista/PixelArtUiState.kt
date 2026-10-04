package the.autarch.pixelista

import androidx.compose.ui.graphics.Color
import the.autarch.pixelista.paletteselection.PaletteSelection
import kotlin.uuid.Uuid

data class PendingSave(
    val name: String,
    val data: List<List<Color>>
)

data class PixelArtUiState(
    val filename: String = Uuid.random().toHexString(),
    val pxField: List<List<Color>> = List(8) { List(8) { Color.Black } },
    val brushColor: Color = Color.Black,
    val paletteSelection: PaletteSelection = PaletteSelection.default,
    val drawGuides: Boolean = true,
    val galleryImages: Map<String, List<List<Color>>> = emptyMap(),
    val isGalleryOpen: Boolean = false,
    val isPaletteSelectorOpen: Boolean = false,
    val showDimensDialog: Boolean = false,
    val showNewImageDialog: Boolean = false,
    val showStorageDialog: Boolean = false,
    val showDeleteDialogName: String? = null,
    val pendingSave: PendingSave? = null
)

sealed interface PixelArtUiEvent {
    data class PixelClicked(val row: Int, val col: Int) : PixelArtUiEvent
    data class BrushSelected(val color: Color) : PixelArtUiEvent
    data class PaletteSelected(val selection: PaletteSelection) : PixelArtUiEvent
    object UndoClicked : PixelArtUiEvent
    object ToggleGuidesClicked : PixelArtUiEvent
    data class ResizeGrid(val rows: Int, val cols: Int) : PixelArtUiEvent
    object NewImageClicked : PixelArtUiEvent
    object ConfirmNewImage : PixelArtUiEvent
    object SaveClicked : PixelArtUiEvent
    data class LoadDrawing(val name: String) : PixelArtUiEvent
    data class RequestDeleteDrawing(val name: String?) : PixelArtUiEvent
    data class ConfirmDeleteDrawing(val name: String) : PixelArtUiEvent
    data class ShowDimensDialog(val show: Boolean) : PixelArtUiEvent
    data class ShowGallery(val show: Boolean) : PixelArtUiEvent
    data class ShowPaletteSelector(val show: Boolean) : PixelArtUiEvent
    data class ShowNewImageDialog(val show: Boolean) : PixelArtUiEvent
    data class ShowStorageDialog(val show: Boolean) : PixelArtUiEvent
    data class StoragePermissionGranted(val uri: String) : PixelArtUiEvent
}
