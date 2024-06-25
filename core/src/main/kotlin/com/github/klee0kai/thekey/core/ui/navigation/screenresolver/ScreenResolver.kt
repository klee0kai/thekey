package com.github.klee0kai.thekey.core.ui.navigation.screenresolver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.WidgetState

interface ScreenResolver {

    @Composable
    fun screenOf(destination: Destination) = Unit

    @Composable
    fun widget(
        modifier: Modifier,
        widgetState: WidgetState,
    ) = Unit

}