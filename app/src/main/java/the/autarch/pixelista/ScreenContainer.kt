package the.autarch.pixelista

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass

@Composable
fun ScreenContainer(
    paletteBar: @Composable (Modifier) -> Unit,
    screen: @Composable (Modifier) -> Unit,
    toolbar: @Composable () -> Unit
) {

    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWidthAtLeastExpanded = sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val modif = Modifier.takeIf({ isWidthAtLeastExpanded })?.then(
        Modifier.windowInsetsPadding(WindowInsets.displayCutout)
    ) ?: Modifier.Companion

    if (isWidthAtLeastExpanded) {
        Row(modif) {
            paletteBar(Modifier.weight(0.15f))
            screen(Modifier.fillMaxHeight().weight(0.85f))
            toolbar()
        }
    } else {
        Column(modif) {
            paletteBar(Modifier.weight(0.15f))
            screen(Modifier.fillMaxWidth().weight(0.85f))
            toolbar()
        }
    }
}