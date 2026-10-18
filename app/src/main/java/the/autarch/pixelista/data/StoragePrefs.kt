package the.autarch.pixelista.data

import android.content.Context
import androidx.core.content.edit

object StoragePrefs {

    private const val PREFS_NAME = "pixelista_prefs"
    private const val KEY_TREE_URI = "tree_uri"

    fun saveTreeUri(context: Context, uriString: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_TREE_URI, uriString) }
    }

    fun getTreeUri(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TREE_URI, null)
    }
}