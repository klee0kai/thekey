@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.core.ui.devkit.components.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import kotlinx.coroutines.delay
import org.jetbrains.annotations.VisibleForTesting
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TimerCircle(
    modifier: Modifier = Modifier,
    buttonSize: Dp = 48.dp,
    interval: Long = 0L,
    endTime: Long = 0L,
    arcColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    var ratio by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(endTime, interval) {
        while (interval > 0) {
            val now = System.currentTimeMillis()
            ratio = (endTime - now) / interval.toFloat()
            delay(16.milliseconds)
        }
    }

    Canvas(modifier = modifier
        .minimumInteractiveComponentSize()
        .size(buttonSize)
        .clip(CircleShape)
        .run {
            when {
                onClick == null && onLongClick == null -> this
                else -> combinedClickable(
                    onLongClick = onLongClick,
                    onClick = { onClick?.invoke() }
                )
            }
        }
    ) {
        drawArc(
            color = arcColor,
            startAngle = -180f,
            sweepAngle = 360f * (1f - ratio),
            useCenter = true,
            style = Fill,
        )
    }

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun TimerCirclePreview() = DebugDarkContentPreview {
    TimerCircle(
        interval = TimeUnit.SECONDS.toMillis(30),
        endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30),
    )
}
