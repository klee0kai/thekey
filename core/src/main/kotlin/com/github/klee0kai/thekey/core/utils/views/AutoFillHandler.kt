package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly

@Composable
fun AutoFillList(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    variants: List<String> = emptyList(),
    onSelected: (String?) -> Unit = {},
) {
    val theme = LocalTheme.current
    val variantsListAlpha by animateAlphaAsState(isVisible && variants.isNotEmpty())
    val notVisible = rememberDerivedStateOf { variantsListAlpha <= 0 }

    if (notVisible.value) return

    LazyColumn(
        modifier = modifier
            .alpha(variantsListAlpha)
            .heightIn(0.dp, 200.dp)
            .background(
                color = theme.colorScheme.popupMenu.surfaceColor,
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        variants.forEach { text ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelected.invoke(text)
                        }
                ) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxWidth()
                    )
                }

            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Preview
@DebugOnly
@Composable
fun AutoFillListPreview() = DebugDarkContentPreview {
    AutoFillList(
        isVisible = true,
        variants = listOf(
            "variant 1",
            "variant 2",
        )
    )
}