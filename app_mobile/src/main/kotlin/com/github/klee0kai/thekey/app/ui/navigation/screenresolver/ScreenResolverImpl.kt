package com.github.klee0kai.thekey.app.ui.navigation.screenresolver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.ui.about.AboutScreen
import com.github.klee0kai.thekey.app.ui.designkit.DesignScreen
import com.github.klee0kai.thekey.app.ui.designkit.EmptyScreen
import com.github.klee0kai.thekey.app.ui.designkit.dialogs.AlertDialogScreen
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.genhist.GenHistScreen
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.navigation.model.AboutDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.DesignDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.app.ui.note.EditNoteScreen
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupsScreen
import com.github.klee0kai.thekey.app.ui.settings.SettingScreen
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreen
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginScreen
import com.github.klee0kai.thekey.app.ui.settings.plugins.PluginsScreen
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import com.github.klee0kai.thekey.core.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.DynamicDestination
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import timber.log.Timber

class ScreenResolverImpl : ScreenResolver {

    @Composable
    override fun screenOf(destination: Destination) {
        when (destination) {
            is LoginDestination -> LoginScreen()
            is SettingsDestination -> SettingScreen()
            is AboutDestination -> AboutScreen()
            is PluginsDestination -> PluginsScreen()
            is PluginDestination -> PluginScreen(destination)
            is StoragesDestination -> StoragesScreen()
            is EditStorageDestination -> EditStorageScreen(path = destination.path)
            is StorageDestination -> StorageScreen(destination)
            is GenHistDestination -> GenHistScreen(destination)
            is EditNoteDestination -> EditNoteScreen(destination)
            is EditNoteGroupDestination -> EditNoteGroupsScreen(destination)

            // dialogs
            is AlertDialogDestination -> AlertDialogScreen(destination)

            // dynamic features
            is DynamicDestination -> PluginDummyScreen(destination)

            // debug
            is DesignDestination -> if (BuildConfig.DEBUG) DesignScreen() else EmptyScreen()
            else -> {
                LaunchedEffect(key1 = Unit) { Timber.e("dest not found $destination") }
                EmptyScreen()
            }
        }
    }

}