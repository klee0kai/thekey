package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.ui.editstorage.presenter.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupPresenter
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

interface PresentersDependencies {

    fun loginPresenter(identifier: StorageIdentifier? = null): LoginPresenter

    fun navigationBoardPresenter(): NavigationBoardPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(identifier: StorageIdentifier? = null): EditStoragePresenter

    fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier): EditStoragesGroupPresenter

    fun storagePresenter(identifier: StorageIdentifier): StoragePresenter

    fun genPasswPresenter(identifier: StorageIdentifier): GenPasswPresenter

    fun genHistPresenter(identifier: StorageIdentifier): GenHistPresenter

    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter

    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter

    fun pluginsPresenter(): PluginsPresenter

    fun pluginPresenter(identifier: DynamicFeature): PluginPresenter

}