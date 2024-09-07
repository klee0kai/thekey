@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.navigation.screenresolver

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.thekey.app.ui.debugflags.DebugFlagsDialog
import com.github.klee0kai.thekey.app.ui.debugflags.DebugFlagsPreferencesWidget
import com.github.klee0kai.thekey.app.ui.navigation.model.DebugFlagsDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.DebugSettingsWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.WidgetState
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

class ScreenResolverDebugExt(
    private val origin: ScreenResolver,
) : ScreenResolver by origin {

    @Composable
    override fun screenOf(destination: Destination) {
        when (destination) {
            is DebugFlagsDialogDestination -> DebugFlagsDialog()
            else -> origin.screenOf(destination = destination)
        }
    }


    @Composable
    override fun widget(modifier: Modifier, widgetState: WidgetState) {
        when (widgetState) {
            is DebugSettingsWidgetState -> {
                DebugFlagsPreferencesWidget()
                origin.widget(modifier, widgetState)
            }

            else -> {
                origin.widget(modifier, widgetState)
            }
        }
    }

}