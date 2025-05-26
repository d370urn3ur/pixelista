package the.autarch.pixelista

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Disk {

    private val _images = MutableStateFlow<Map<String, List<List<Color>>>>(emptyMap())
    val images = _images.asStateFlow()

    fun save(name: String, data: List<List<Color>>) {
        val old = _images.value.toMutableMap()
        old[name] = data
        _images.value = old.toMap()
    }

    fun load(name: String): List<List<Color>>? = images.value[name]
}