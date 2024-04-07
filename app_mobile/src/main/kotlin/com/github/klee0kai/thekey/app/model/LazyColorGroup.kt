package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.utils.common.LazyModel
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyColorGroup = LazyModel<ColorGroup, ColorGroup>

val LazyColorGroup.id get() = placeholder.id

fun dummyLazyColorGroup() = LazyColorGroup(ColorGroup()) {
    delay(Random.nextLong(3000))
    ColorGroup()
}
