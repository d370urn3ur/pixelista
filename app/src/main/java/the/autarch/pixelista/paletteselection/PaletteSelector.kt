package the.autarch.pixelista.paletteselection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import the.autarch.pixelista.R

@Composable
fun PaletteSelector(
    currentSelection: PaletteSelection,
    modifier: Modifier = Modifier,
    onSelect: (PaletteSelection) -> Unit,
) {

    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {}
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (selection in PaletteSelection.database) {
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(onClick = { onSelect(selection) }) {
                        Text(selection.name)
                    }

                    if (currentSelection == selection) {
                        Image(
                            painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    selection.reference?.let { link ->
                        IconButton({
                            uriHandler.openUri(link)
                        }) {
                            Icon(
                                painterResource(R.drawable.ic_info),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPaletteSelector() {
    PaletteSelector(
        currentSelection = PaletteSelection.default,
        onSelect = {}
    )
}