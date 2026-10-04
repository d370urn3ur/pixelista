package the.autarch.pixelista.dialog

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DimensDialogWheel(currHeight: Int, currWidth: Int, onCancel: () -> Unit, onAccept: (Pair<Int,Int>) -> Unit) {

    val (width, setWidth) = remember { mutableFloatStateOf(currWidth.toFloat()) }
    val (height, setHeight) = remember { mutableFloatStateOf(currHeight.toFloat()) }

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
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    Modifier.padding(16.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Width",
                            style = MaterialTheme.typography.titleMedium
                        )

                        NumberWheel(
                            width.toInt(),
                            { setWidth(it.toFloat()) }
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Height",
                            style = MaterialTheme.typography.titleMedium
                        )

                        NumberWheel(
                            height.toInt(),
                            { setHeight(it.toFloat()) }
                        )
                    }
                }

                Row(Modifier.padding(16.dp).align(Alignment.End)) {
                    TextButton(onCancel) {
                        Text("Cancel".uppercase())
                    }
                    TextButton({
                        try {
                            onAccept(Pair(height.toInt(), width.toInt()))
                        } catch (t: Throwable) {
                            Log.e("DimensDialog", t.localizedMessage ?: "unknown error")
                        }
                    }) {
                        Text("Update".uppercase())
                    }
                }
            }
        }
    }
}

@Composable
fun DimensDialogSlider(currHeight: Int, currWidth: Int, onCancel: () -> Unit, onAccept: (Pair<Int,Int>) -> Unit) {

    val (width, setWidth) = remember { mutableFloatStateOf(currWidth.toFloat()) }
    val (height, setHeight) = remember { mutableFloatStateOf(currHeight.toFloat()) }

    val steps = 23
    val valueRange = 8f..32f

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
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    DimensSlider(
                        title = "Width",
                        value = width,
                        onValueChange = setWidth,
                        steps = steps,
                        valueRange = valueRange
                    )

                    DimensSlider(
                        title = "Height",
                        value = height,
                        onValueChange = setHeight,
                        steps = steps,
                        valueRange = valueRange
                    )
                }
                Row(Modifier.padding(16.dp).align(Alignment.End)) {
                    TextButton(onCancel) {
                        Text("Cancel".uppercase())
                    }
                    TextButton({
                        try {
                            onAccept(Pair(height.toInt(), width.toInt()))
                        } catch (t: Throwable) {
                            Log.e("DimensDialog", t.localizedMessage ?: "unknown error")
                        }
                    }) {
                        Text("Update".uppercase())
                    }
                }
            }
        }
    }
}

@Composable
fun DimensSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    steps: Int,
    valueRange: ClosedFloatingPointRange<Float>
) {

    Column {

        Text(
            title,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(44.dp)) {
                Text(
                    "${value.toInt()}",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                steps = steps,
                valueRange = valueRange
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewDimensDialog_Wheel() {
    DimensDialogWheel(
        currHeight = 8,
        currWidth = 8,
        onCancel = {},
        onAccept = {}
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewDimensDialog_Slider() {
    DimensDialogSlider(
        currHeight = 8,
        currWidth = 8,
        onCancel = {},
        onAccept = {}
    )
}