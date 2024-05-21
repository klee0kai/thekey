package com.github.klee0kai.thekey.core.ui.navigation.model

import dev.olshevski.navigation.reimagined.NavEntry
import dev.olshevski.navigation.reimagined.NavId

data class NavigateBackstackChange(
    val currentNavStack: List<NavEntry<*>>,
    val closedDestination: Pair<NavId, Any?>? = null,
) {
    val currentNavIds by lazy { currentNavStack.map { it.id } }
}