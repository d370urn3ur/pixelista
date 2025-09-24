package the.autarch.pixelista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND

object Palette {

    @Composable
    fun ColorPalette(currentColor: Color, modifier: Modifier = Modifier, onChangeBrushColor: (Color) -> Unit) {

        val topColors = listOf(
            Color.Black,
            Color(0xff273746),
            Color(0xff8e44ad),
            Color(0xff196f3d),
            Color( 0xff935116),
            Color(0xff424949),
            Color(0xffe5e7e9),
            Color.White
        )
        val bottomColors = listOf(
            Color(0xffe74c3c),
            Color(0xfff39c12),
            Color(0xfff4d03f),
            Color(0xff2ecc71),
            Color(0xff3498db),
            Color(0xff512e5f),
            Color(0xffff9ad1),
            Color(0xffffe29a)
        )

        val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isWidthAtLeastExpanded = sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

        if (isWidthAtLeastExpanded) {
            Row(modifier) {
                ColorColumn(topColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
                ColorColumn(bottomColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
            }
        } else {
            Column(modifier) {
                ColorRow(topColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
                ColorRow(bottomColors, currentColor, Modifier.weight(1f), onChangeBrushColor)
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