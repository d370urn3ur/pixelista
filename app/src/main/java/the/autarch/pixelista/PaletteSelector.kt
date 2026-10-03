package the.autarch.pixelista

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PaletteSelector(
    currentSelection: PaletteSelection,
    modifier: Modifier = Modifier.Companion,
    onSelect: (PaletteSelection) -> Unit,
) {
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

                    Button(
                        onClick = { onSelect(selection) },
                    ) {
                        Text(selection.name)
                    }

                    if (currentSelection == selection) {
                        Image(
                            painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}