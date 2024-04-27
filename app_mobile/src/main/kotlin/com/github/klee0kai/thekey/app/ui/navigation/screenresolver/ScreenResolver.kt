package com.github.klee0kai.thekey.app.ui.navigation.screenresolver

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination

interface ScreenResolver {

    @Composable
    fun screenOf(destination: Destination)

}