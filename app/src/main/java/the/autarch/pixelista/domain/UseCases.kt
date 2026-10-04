package the.autarch.pixelista.domain

import androidx.compose.ui.graphics.Color
import the.autarch.pixelista.data.PixelArtRepository

class SaveDrawingUseCase(private val repository: PixelArtRepository) {
    operator fun invoke(name: String, data: List<List<Color>>) {
        repository.save(name, data)
    }
}

class LoadDrawingUseCase(private val repository: PixelArtRepository) {
    operator fun invoke(name: String): List<List<Color>>? {
        return repository.load(name)
    }
}

class DeleteDrawingUseCase(private val repository: PixelArtRepository) {
    operator fun invoke(name: String) {
        repository.delete(name)
    }
}
