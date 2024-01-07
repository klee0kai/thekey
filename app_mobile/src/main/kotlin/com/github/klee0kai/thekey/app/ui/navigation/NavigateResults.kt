package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.NavigateResults.navChanges
import com.github.klee0kai.thekey.app.ui.navigation.NavigateResults.resultsScope
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

data class NavigateBackstackChange(
    val currentNavIds: List<NavId>,
    val closedDestination: Pair<NavId, Any?>? = null,
)

object NavigateResults {
    val navChanges = MutableSharedFlow<NavigateBackstackChange>(replay = 1)
    val resultsScope = DI.mainThreadScope()
}


@Composable
fun <T> NavController<T>.cleanNotUselessResultFlows() {
    LaunchedEffect(backstack.entries.map { it.id }.hashCode()) {
        delay(10)
        navChanges.emit(NavigateBackstackChange(
            currentNavIds = backstack.entries.map { it.id }
        ))
    }
}

inline fun <reified R> NavController<Destination>.navigateForResult(destination: Destination): Flow<R> {
    val navEntry = navEntry(destination)
    setNewBackstack(
        entries = backstack.entries + navEntry,
        action = NavAction.Navigate
    )
    return flow<R> {
        delay(50)
        val result = navChanges
            .first { change ->
                val destClosed = change.closedDestination?.first == navEntry.id
                val destinationLost = !change.currentNavIds.contains(navEntry.id)
                destClosed || destinationLost
            }
        if (result.closedDestination?.first == navEntry.id) {
            (result.closedDestination.second as? R)?.let { emit(it) }
        }
    }.shareLatest(resultsScope)
}

fun <R> NavController<Destination>.backWithResult(
    result: R,
    exitFromApp: Boolean = false,
): Boolean {
    val navId = backstack.entries.last().id
    val popResult = pop()
    resultsScope.launch {
        navChanges.emit(
            NavigateBackstackChange(
                currentNavIds = backstack.entries.map { it.id },
                closedDestination = navId to result
            )
        )
    }
    if (!popResult && exitFromApp) {
        DI.backDispatcher().onBackPressed()
        return true
    }
    return popResult
}

fun NavController<Destination>.back() {
    DI.backDispatcher().onBackPressed()
}