package com.github.klee0kai.thekey.dynamic.findstorage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesStatusBarWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.WidgetState
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets.FSStoragesButtonsWidget
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets.FSStoragesListWidget
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets.FSStoragesStatusBarWidget

class FSScreenResolverExt(
    private val origin: ScreenResolver,
) : ScreenResolver by origin {


    @Composable
    override fun widget(modifier: Modifier, widgetState: WidgetState) {
        when (widgetState) {
            is StoragesListWidgetState -> FSStoragesListWidget(modifier, widgetState, parent())
            is StoragesButtonsWidgetState -> FSStoragesButtonsWidget(modifier, widgetState, parent())
            is StoragesStatusBarWidgetState -> FSStoragesStatusBarWidget(modifier, widgetState, parent())

            else -> origin.widget(modifier, widgetState)
        }
    }

    private fun <T : WidgetState> parent(): @Composable (modifier: Modifier, state: T) -> Unit =
        { modifier, state -> origin.widget(modifier = modifier, widgetState = state) }

}