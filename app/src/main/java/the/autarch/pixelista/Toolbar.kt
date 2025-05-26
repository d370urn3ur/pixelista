package the.autarch.pixelista

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

object Toolbar {

    @Composable
    fun Tools(
        showingGuides: Boolean,
        modifier: Modifier = Modifier,
        onClickUndo: () -> Unit,
        onClickToggleGuides: () -> Unit,
        onChangeFieldDimensions: () -> Unit,
        onNewImage: () -> Unit,
        onSave: () -> Unit,
        onOpenGallery: () -> Unit
    ) {
        Row(modifier) {

            Spacer(Modifier.weight(1f))

            IconButton(onClickUndo) {
                Icon(painterResource(R.drawable.ic_undo), contentDescription = "")
            }

            IconButton(onClickToggleGuides) {
                Icon(
                    if (showingGuides) painterResource(R.drawable.ic_grid_off)
                    else painterResource(R.drawable.ic_grid_on),
                    contentDescription = ""
                )
            }

            IconButton(onChangeFieldDimensions) {
                Icon(painterResource(R.drawable.ic_arrows_out), contentDescription = "")
            }

            IconButton(onNewImage) {
                Icon(painterResource(R.drawable.ic_new_file), contentDescription = "")
            }

            IconButton(onSave) {
                Icon(painterResource(R.drawable.ic_save), contentDescription = "")
            }

            IconButton(onOpenGallery) {
                Icon(painterResource(R.drawable.ic_gallery), contentDescription = "")
            }

            Spacer(Modifier.weight(1f))
        }
    }
}