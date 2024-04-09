package com.github.klee0kai.thekey.app.domain.model

import com.github.klee0kai.thekey.app.utils.common.Dummy
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.awaitCancellation

typealias LazyColorGroup = LazyModel<ColorGroup, ColorGroup>

val LazyColorGroup.id get() = placeholder.id

fun dummyLazyColorGroupSkeleton(id: Long = Dummy.dummyId) = LazyModelProvider<ColorGroup,ColorGroup>(ColorGroup(id = id)) {
    awaitCancellation()
}

fun dummyLazyColorGroup(colorGroup: ColorGroup = ColorGroup(id = Dummy.dummyId)) = LazyModelProvider(colorGroup, preloaded = colorGroup) {
    colorGroup
}
