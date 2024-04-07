package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyColorGroup = LazyModel<ColorGroup, ColorGroup>

val LazyColorGroup.id get() = placeholder.id

fun dummyLazyColorGroup() = LazyModelProvider(ColorGroup()) {
    delay(Random.nextLong(3000))
    ColorGroup()
}
