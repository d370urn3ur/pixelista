package the.autarch.pixelista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND

object Palette {

    @Composable
    fun ColorPalette(
        paletteSelection: PaletteSelection,
        currentColor: Color,
        modifier: Modifier = Modifier,
        onChangeBrushColor: (Color) -> Unit
    ) {

        val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
        val isWidthAtLeastExpanded = sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

        if (isWidthAtLeastExpanded) {
            Row(modifier) {
                ColorColumn(paletteSelection.topColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
                ColorColumn(paletteSelection.bottomColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
            }
        } else {
            Column(modifier) {
                ColorRow(paletteSelection.topColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
                ColorRow(paletteSelection.bottomColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
            }
        }
    }

    @Composable
    fun ColorRow(colors: List<Color>, currentColor: Color, modifier: Modifier = Modifier, onClick: (Color) -> Unit) {
        Row(modifier) {
            ColorStack(colors, currentColor, Modifier.fillMaxHeight().weight(1f), onClick)
        }
    }

    @Composable
    fun ColorColumn(colors: List<Color>, currentColor: Color, modifier: Modifier = Modifier, onClick: (Color) -> Unit) {
        Column(modifier) {
            ColorStack(colors, currentColor, Modifier.fillMaxWidth().weight(1f), onClick)
        }
    }

    @Composable
    fun ColorStack(colors: List<Color>, currentColor: Color, modifier: Modifier = Modifier, onClick: (Color) -> Unit) {
        for (color in colors) {
            val border = if (color == currentColor) {
                modifier.border(width = 5.dp, Color.Magenta, RectangleShape)
            } else {
                modifier
            }
            Box(
                border.background(color).clickable { onClick(color) }
            ) {}
        }
    }
}