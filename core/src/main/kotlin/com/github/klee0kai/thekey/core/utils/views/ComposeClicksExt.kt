package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Composable
@NonRestartableComposable
fun rememberClick(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    launch: () -> Unit,
): () -> Unit = remember(key1, key2, key3, key4, key5, key6) {
    launch
}


@Composable
@NonRestartableComposable
fun rememberClickDebounced(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: () -> Unit,
): () -> Unit {
    var lastClickTime by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        {
            val now = TimeSource.Monotonic.markNow()
            if (lastClickTime?.let { now - it < debounce } != true) {
                launch()
                lastClickTime = now
            }
        }
    }
}

