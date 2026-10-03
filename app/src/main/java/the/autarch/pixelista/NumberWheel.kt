package the.autarch.pixelista

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NumberWheel(
    initialValue: Int,
    onValueChange: (Int) -> Unit
) {
    val min = 8
    val max = 32
    // Reversed items: 32 at top, 8 at bottom
    val items = remember { (min..max).reversed().toList() }

    val itemHeight = 48.dp
    // Show 3 visible items: above, center, below
    val visibleItemsCount = 3
    val wheelHeight = itemHeight * visibleItemsCount

    val initialIndex = max - initialValue.coerceIn(min, max)
    // Scroll to initialIndex so initialIndex + 1 (the target value) is centered
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    // Determine currently centered item based on scroll position (subtracting 1 for top spacer)
    val selectedIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                initialIndex
            } else {
                val viewportCenter = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                val centeredItem = visibleItems.minByOrNull { item ->
                    kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
                }
                val lazyIndex = centeredItem?.index ?: initialIndex
                (lazyIndex - 1).coerceIn(items.indices)
            }
        }
    }

    // Notify parent when selected index changes
    LaunchedEffect(selectedIndex) {
        if (selectedIndex in items.indices) {
            onValueChange(items[selectedIndex])
        }
    }

    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up Arrow (moves towards 32 / higher numbers)
        TextButton(onClick = {
            coroutineScope.launch {
                val targetIndex = (selectedIndex - 1).coerceAtLeast(0)
                listState.animateScrollToItem(targetIndex)
            }
        }) {
            Text("▲", style = MaterialTheme.typography.titleLarge)
        }

        // Wheel Box with 3 visible items height
        Box(
            modifier = Modifier
                .height(wheelHeight)
                .width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top spacer so first item can scroll to center (LazyColumn index 0)
                item { Spacer(modifier = Modifier.height(itemHeight)) }

                itemsIndexed(items) { index, number ->
                    val isSelected = index == selectedIndex
                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.3f,
                        label = "alpha"
                    )
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.alpha(alpha)
                        )
                    }
                }

                // Bottom spacer so last item can scroll to center
                item { Spacer(modifier = Modifier.height(itemHeight)) }
            }
        }

        // Down Arrow (moves towards 8 / lower numbers)
        TextButton(onClick = {
            coroutineScope.launch {
                val targetIndex = (selectedIndex + 1).coerceAtMost(items.size - 1)
                listState.animateScrollToItem(targetIndex)
            }
        }) {
            Text("▼", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewNumberWheel() {

    var currentNumber by remember { mutableIntStateOf(10) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        NumberWheel(
            initialValue = currentNumber,
            onValueChange = { currentNumber = it }
        )

        Text("$currentNumber", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.weight(1f))
    }
}
