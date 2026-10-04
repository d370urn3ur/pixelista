package the.autarch.pixelista.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow

class PixelArtRepository(context: Context) {

    private val disk = Disk(context)

    val images: StateFlow<Map<String, List<List<Color>>>> = disk.images

    fun reloadDb() {
        disk.reloadDb()
    }

    fun save(name: String, data: List<List<Color>>) {
        disk.save(name, data)
    }

    fun load(name: String): List<List<Color>>? {
        return disk.load(name)
    }

    fun delete(name: String) {
        disk.delete(name)
    }
}
