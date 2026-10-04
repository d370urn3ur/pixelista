package the.autarch.pixelista.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import the.autarch.pixelista.R

@Composable
fun StorageDialog(
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {
    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select your storage location",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Select a storage location to save your images. If you have images from a previous save, they will be loaded into the gallery.")

                Row(
                    Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onCancel) {
                        Text(stringResource(R.string.no))
                    }

                    TextButton({ onAccept() }) {
                        Text(stringResource(R.string.yes))
                    }
                }
            }
        }
    }
}