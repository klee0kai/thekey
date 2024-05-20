package com.github.klee0kai.thekey.core.ui.navigation.screenresolver

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination

interface ScreenResolver {

    @Composable
    fun screenOf(destination: Destination)

}