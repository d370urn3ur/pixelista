package the.autarch.pixelista

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import the.autarch.pixelista.data.History
import the.autarch.pixelista.data.Operation
import the.autarch.pixelista.data.PixelArtRepository
import the.autarch.pixelista.domain.DeleteDrawingUseCase
import the.autarch.pixelista.domain.LoadDrawingUseCase
import the.autarch.pixelista.domain.SaveDrawingUseCase
import kotlin.uuid.Uuid

class PixelArtViewModel(
    private val repository: PixelArtRepository,
    private val saveDrawingUseCase: SaveDrawingUseCase,
    private val loadDrawingUseCase: LoadDrawingUseCase,
    private val deleteDrawingUseCase: DeleteDrawingUseCase
) : ViewModel() {

    private val history = History()

    private val _uiState = MutableStateFlow(PixelArtUiState())
    val uiState: StateFlow<PixelArtUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.images.collect { images ->
                _uiState.update { it.copy(galleryImages = images) }
            }
        }
    }

    fun onEvent(event: PixelArtUiEvent) {
        when (event) {
            is PixelArtUiEvent.PixelClicked -> {
                val row = event.row
                val col = event.col
                val currentField = _uiState.value.pxField
                val rows = currentField.size
                val cols = currentField.firstOrNull()?.size ?: 0
                val brushColor = _uiState.value.brushColor

                if (currentField[row][col] != brushColor) {
                    history.addOperation(Operation(row, col, currentField[row][col]))
                }

                val updatedField = List(rows) { r ->
                    List(cols) { c ->
                        if (r == row && c == col) brushColor
                        else currentField[r][c]
                    }
                }
                _uiState.update { it.copy(pxField = updatedField) }
            }
            is PixelArtUiEvent.BrushSelected -> {
                _uiState.update { it.copy(brushColor = event.color) }
            }
            is PixelArtUiEvent.PaletteSelected -> {
                _uiState.update { it.copy(paletteSelection = event.selection) }
            }
            is PixelArtUiEvent.UndoClicked -> {
                val currentField = _uiState.value.pxField
                val updatedField = history.undo(currentField)
                _uiState.update { it.copy(pxField = updatedField) }
            }
            is PixelArtUiEvent.ToggleGuidesClicked -> {
                _uiState.update { it.copy(drawGuides = !it.drawGuides) }
            }
            is PixelArtUiEvent.ResizeGrid -> {
                val updatedField = List(event.rows) { List(event.cols) { Color.Black } }
                history.clear()
                _uiState.update {
                    it.copy(
                        pxField = updatedField,
                        showDimensDialog = false
                    )
                }
            }
            is PixelArtUiEvent.NewImageClicked -> {
                _uiState.update { it.copy(showNewImageDialog = true) }
            }
            is PixelArtUiEvent.ConfirmNewImage -> {
                val currentField = _uiState.value.pxField
                val rows = currentField.size
                val cols = currentField.firstOrNull()?.size ?: 0
                val newField = List(rows) { List(cols) { Color.Black } }
                history.clear()
                _uiState.update {
                    it.copy(
                        filename = Uuid.random().toHexString(),
                        pxField = newField,
                        showNewImageDialog = false
                    )
                }
            }
            is PixelArtUiEvent.SaveClicked -> {
                val state = _uiState.value
                saveDrawingUseCase(state.filename, state.pxField)
            }
            is PixelArtUiEvent.LoadDrawing -> {
                loadDrawingUseCase(event.name)?.let { data ->
                    history.clear()
                    _uiState.update {
                        it.copy(
                            filename = event.name,
                            pxField = data,
                            isGalleryOpen = false
                        )
                    }
                }
            }
            is PixelArtUiEvent.RequestDeleteDrawing -> {
                _uiState.update { it.copy(showDeleteDialogName = event.name) }
            }
            is PixelArtUiEvent.ConfirmDeleteDrawing -> {
                deleteDrawingUseCase(event.name)
                _uiState.update { it.copy(showDeleteDialogName = null) }
            }
            is PixelArtUiEvent.ShowDimensDialog -> {
                _uiState.update { it.copy(showDimensDialog = event.show) }
            }
            is PixelArtUiEvent.ShowGallery -> {
                _uiState.update { it.copy(isGalleryOpen = event.show) }
            }
            is PixelArtUiEvent.ShowPaletteSelector -> {
                _uiState.update { it.copy(isPaletteSelectorOpen = event.show) }
            }
            is PixelArtUiEvent.ShowNewImageDialog -> {
                _uiState.update { it.copy(showNewImageDialog = event.show) }
            }
            is PixelArtUiEvent.ShowStorageDialog -> {
                _uiState.update { it.copy(showStorageDialog = event.show) }
            }
            is PixelArtUiEvent.StoragePermissionGranted -> {
                _uiState.value.pendingSave?.let { pending ->
                    saveDrawingUseCase(pending.name, pending.data)
                    _uiState.update { it.copy(pendingSave = null) }
                }
                repository.reloadDb()
            }
        }
    }

    fun setPendingSave(name: String, data: List<List<Color>>) {
        _uiState.update { it.copy(pendingSave = PendingSave(name, data)) }
    }
}
