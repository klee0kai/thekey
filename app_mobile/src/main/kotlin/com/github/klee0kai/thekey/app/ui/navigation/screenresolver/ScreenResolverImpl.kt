@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.navigation.screenresolver

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.ui.about.AboutScreen
import com.github.klee0kai.thekey.app.ui.changepassw.ChangeStoragePasswordScreen
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.hist.GenHistScreen
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.navigation.model.AboutDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.ChangeStoragePasswordDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.DesignDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.HistDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.SelectStorageDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.app.ui.note.NoteDialog
import com.github.klee0kai.thekey.app.ui.noteedit.EditNoteScreen
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupsScreen
import com.github.klee0kai.thekey.app.ui.settings.SettingScreen
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreen
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginScreen
import com.github.klee0kai.thekey.app.ui.settings.plugins.PluginsScreen
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storagegroup.EditStorageGroupsScreen
import com.github.klee0kai.thekey.app.ui.storages.SelectStorageDialog
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import com.github.klee0kai.thekey.app.ui.storages.widgets.ColoredStorageItemWidget
import com.github.klee0kai.thekey.app.ui.storages.widgets.StoragesButtonsWidget
import com.github.klee0kai.thekey.app.ui.storages.widgets.StoragesListWidget
import com.github.klee0kai.thekey.app.ui.storages.widgets.StoragesStatusBarWidget
import com.github.klee0kai.thekey.core.ui.devkit.DesignScreen
import com.github.klee0kai.thekey.core.ui.devkit.EmptyScreen
import com.github.klee0kai.thekey.core.ui.devkit.dialogs.AlertDialogScreen
import com.github.klee0kai.thekey.core.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.DynamicDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.StorageItemWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesStatusBarWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.WidgetState
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import timber.log.Timber

class ScreenResolverImpl : ScreenResolver {

    @Composable
    override fun screenOf(destination: Destination) {
        when (destination) {
            is LoginDestination -> LoginScreen(destination)
            is SettingsDestination -> SettingScreen()
            is AboutDestination -> AboutScreen()
            is PluginsDestination -> PluginsScreen()
            is PluginDestination -> PluginScreen(destination)
            is StoragesDestination -> StoragesScreen()
            is EditStorageDestination -> EditStorageScreen(path = destination.path)
            is ChangeStoragePasswordDestination -> ChangeStoragePasswordScreen(path = destination.path)
            is EditStorageGroupDestination -> EditStorageGroupsScreen(destination)
            is StorageDestination -> StorageScreen(destination)
            is HistDestination -> GenHistScreen(destination)
            is NoteDestination -> NoteDialog(destination)
            is EditNoteDestination -> EditNoteScreen(destination)
            is EditNoteGroupDestination -> EditNoteGroupsScreen(destination)

            // dialogs
            is AlertDialogDestination -> AlertDialogScreen(destination)
            is SelectStorageDialogDestination -> SelectStorageDialog()

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

    @Composable
    override fun widget(
        modifier: Modifier,
        widgetState: WidgetState,
    ) {
        when (widgetState) {
            is StoragesListWidgetState -> StoragesListWidget(modifier, widgetState)
            is StoragesButtonsWidgetState -> StoragesButtonsWidget(modifier, widgetState)
            is StoragesStatusBarWidgetState -> StoragesStatusBarWidget(modifier, widgetState)
            is StorageItemWidgetState -> ColoredStorageItemWidget(modifier, widgetState)
        }
    }

}