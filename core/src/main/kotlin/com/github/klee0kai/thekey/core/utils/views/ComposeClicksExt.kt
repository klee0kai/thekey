package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

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
    var lastClickTime by remember { mutableStateOf<Long?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        {
            val now = System.currentTimeMillis()
            if (lastClickTime?.let { now - it < debounce.inWholeMilliseconds } != true) {
                launch()
                lastClickTime = now
            }
        }
    }
}


@Composable
@NonRestartableComposable
fun <Arg> rememberClickDebouncedArg(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: (Arg) -> Unit,
): (Arg) -> Unit {
    var lastClickTime by remember { mutableStateOf<Long?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        { arg: Arg ->
            val now = System.currentTimeMillis()
            if (lastClickTime?.let { now - it < debounce.inWholeMilliseconds } != true) {
                launch(arg)
                lastClickTime = now
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun <Arg> rememberClickArg(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    launch: (Arg) -> Unit,
): (Arg) -> Unit = rememberClickDebouncedArg(key1, key2, key3, key4, key5, key6, ZERO, launch)

@Composable
@NonRestartableComposable
fun <Arg, Arg2> rememberClickDebouncedArg2(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: (Arg, Arg2) -> Unit,
): (Arg, Arg2) -> Unit {
    var lastClickTime by remember { mutableStateOf<Long?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        { arg: Arg, arg2: Arg2 ->
            val now = System.currentTimeMillis()
            if (lastClickTime?.let { now - it < debounce.inWholeMilliseconds } != true) {
                launch(arg, arg2)
                lastClickTime = now
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun <Cons> rememberClickDebouncedCons(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: Cons.() -> Unit,
): Cons.() -> Unit {
    var lastClickTime by remember { mutableStateOf<Long?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        {
            val now = System.currentTimeMillis()
            if (lastClickTime?.let { now - it < debounce.inWholeMilliseconds } != true) {
                launch()
                lastClickTime = now
            }
        }
    }
}

