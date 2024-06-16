package com.github.klee0kai.thekey.dynamic.findstorage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetId
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetId
import com.github.klee0kai.thekey.core.ui.navigation.model.WidgetId
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets.FSStoragesButtonsWidget
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets.FSStoragesListWidget

class FSScreenResolverExt(
    private val origin: ScreenResolver,
) : ScreenResolver by origin {

    @Composable
    override fun widget(modifier: Modifier, widgetId: WidgetId) {
        when (widgetId) {
            is StoragesListWidgetId -> FSStoragesListWidget(widgetId)
            is StoragesButtonsWidgetId -> FSStoragesButtonsWidget(widgetId)

            else -> origin.widget(modifier, widgetId)
        }
    }

}