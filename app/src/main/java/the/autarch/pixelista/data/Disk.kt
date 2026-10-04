package the.autarch.pixelista.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import androidx.core.net.toUri

class Disk(context: Context) {

    companion object {
        private const val fileDb: String = "user_pixels.json"
    }

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _images = MutableStateFlow<Map<String, List<List<Color>>>>(emptyMap())
    val images = _images.asStateFlow()

    init {
        scope.launch {
            reloadDb()
        }
    }

    fun reloadDb() {
        _images.value = loadFromDisk()
    }

    fun save(name: String, data: List<List<Color>>) {
        val old = _images.value.toMutableMap()
        old[name] = data
        _images.value = old.toMap()

        scope.launch {
            persistToDisk(_images.value)
        }
    }

    fun load(name: String): List<List<Color>>? = images.value[name]

    fun delete(name: String) {
        val old = _images.value.toMutableMap()
        old.remove(name)
        _images.value = old.toMap()
        scope.launch {
            persistToDisk(_images.value)
        }
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
            val file = getTargetFile() ?: return

            appContext.contentResolver.openOutputStream(file.uri, "w")?.use { outputStream ->
                outputStream.write(contents.toByteArray())
            }
        } catch (t: Throwable) {
            Log.e("DISK", "json encoding", t)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadFromDisk(): Map<String, List<List<Color>>> {
        return try {

            val file = getTargetFile() ?: return emptyMap()
            if (!file.exists() || file.length() == 0L) return emptyMap()

            val jsonData = appContext.contentResolver.openInputStream(file.uri)?.use { inputStream ->
                Json.decodeFromStream<Map<String, List<List<ULong>>>>(inputStream)
            } ?: return emptyMap()

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

    private fun getTargetFile(): DocumentFile? {
        val treeUriString = StoragePrefs.getTreeUri(appContext) ?: return null
        val treeUri = treeUriString.toUri()
        val rootDir = DocumentFile.fromTreeUri(appContext, treeUri) ?: return null
        return rootDir.findFile(fileDb) ?: rootDir.createFile("application/json", fileDb)
    }
}