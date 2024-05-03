package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.ui.dynamic.presenter.DynamicFeaturePresenter
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

interface PresentersDependencies {

    fun loginPresenter(): LoginPresenter

    fun navigationBoardPresenter(): NavigationBoardPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(identifier: StorageIdentifier? = null): CreateStoragePresenter

    fun storagePresenter(identifier: StorageIdentifier): StoragePresenter

    fun genPasswPresenter(identifier: StorageIdentifier): GenPasswPresenter

    fun genHistPresenter(identifier: StorageIdentifier): GenHistPresenter

    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter

    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter

    fun pluginsPresenter(): PluginsPresenter

    fun pluginPresenter(identifier: PluginIdentifier): PluginPresenter

    fun dynamicFeaturePresenter(feature: DynamicFeature): DynamicFeaturePresenter

}