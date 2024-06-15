package com.github.klee0kai.thekey.dynamic.findstorage.ui.navigation

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

class FSScreenResolverExt(
    private val origin: ScreenResolver,
) : ScreenResolver by origin {

    @Composable
    override fun screenOf(destination: Destination) {
        when (destination) {
            is StoragesDestination -> StoragesScreen()
            is EditStorageDestination -> EditStorageScreen(path = destination.path)

            else -> origin.screenOf(destination)
        }
    }
}