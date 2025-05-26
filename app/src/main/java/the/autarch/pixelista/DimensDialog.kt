package the.autarch.pixelista

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DimensDialog(currWidth: Int, currHeight: Int, onCancel: () -> Unit, onAccept: (Pair<Int,Int>) -> Unit) {

    val (width, setWidth) = remember { mutableStateOf(currWidth.toString()) }
    val (height, setHeight) = remember { mutableStateOf(currHeight.toString()) }

    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Text(
                    text = "Set Dimensions",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                )
                Column(
                    Modifier.padding(16.dp)
                ) {
                    TextField(width, setWidth, label = {
                        Text("Width")
                    })

                    TextField(height, setHeight, label = {
                        Text("Height")
                    })
                }
                Row(Modifier.padding(16.dp).align(Alignment.End)) {
                    TextButton(onCancel) {
                        Text("Cancel")
                    }
                    TextButton({
                        try {
                            onAccept(Pair(width.toInt(), height.toInt()))
                        } catch (t: Throwable) {
                            Log.e("DimensDialog", t.localizedMessage ?: "unknown error")
                        }
                    }) {
                        Text("Update")
                    }
                }
            }
        }
    }
}