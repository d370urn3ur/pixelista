package the.autarch.pixelista

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class Disk(context: Context) {

    companion object {
        private const val fileDb: String = "user_pixels.json"
    }

    private val appContext = context.applicationContext
    private val _images = MutableStateFlow<Map<String, List<List<Color>>>>(emptyMap())
    val images = _images.asStateFlow()

    init {
        _images.value = loadFromDisk()
    }

    fun save(name: String, data: List<List<Color>>) {
        val old = _images.value.toMutableMap()
        old[name] = data
        _images.value = old.toMap()

        persistToDisk(_images.value)
    }

    fun load(name: String): List<List<Color>>? = images.value[name]

    fun delete(name: String) {
        val old = _images.value.toMutableMap()
        old.remove(name)
        _images.value = old.toMap()
        persistToDisk(_images.value)
    }

    private fun persistToDisk(data: Map<String, List<List<Color>>>) {
        val encodable = data.mapValues { entry ->
            entry.value.map { rows ->
                rows.map { color ->
                    color.value
                }
            }
        }
        try {
            val contents = Json.encodeToString(encodable)
            appContext.openFileOutput(fileDb, Context.MODE_PRIVATE).use {
                it.write(contents.toByteArray())
            }
        } catch (t: Throwable) {
            Log.e("DISK", "json encoding", t)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadFromDisk(): Map<String, List<List<Color>>> {
        return try {
            val jsonData = Json.decodeFromStream<Map<String, List<List<ULong>>>>(
                appContext.openFileInput(fileDb)
            )
            jsonData.mapValues { entry ->
                entry.value.map { rows ->
                    rows.map { colorValue ->
                        Color(colorValue)
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e("DISK", "decode error", t)
            emptyMap()
        }
    }
}