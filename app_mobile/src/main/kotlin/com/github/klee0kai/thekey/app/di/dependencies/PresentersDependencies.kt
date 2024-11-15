package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.ui.changepassw.presenter.ChangeStoragePasswordPresenter
import com.github.klee0kai.thekey.app.ui.editstorage.presenter.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.hist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.hist.presenter.NoteHistPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.main.presenter.MainPresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.NotePresenter
import com.github.klee0kai.thekey.app.ui.noteedit.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenter
import com.github.klee0kai.thekey.app.ui.otpnote.presenter.OtpNotePresenter
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.settings.presenter.SettingsPresenter
import com.github.klee0kai.thekey.app.ui.simpleboard.presenter.SimpleBoardPresenter
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

    fun mainPresenter(): MainPresenter

    fun loginPresenter(identifier: StorageIdentifier = StorageIdentifier()): LoginPresenter

    fun simpleBoardPresenter(): SimpleBoardPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(identifier: StorageIdentifier? = null): EditStoragePresenter

    fun changeStoragePasswordPresenter(storageIdentifier: StorageIdentifier?): ChangeStoragePasswordPresenter

    fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier): EditStoragesGroupPresenter

    fun storagePresenter(identifier: StorageIdentifier): StoragePresenter

    fun genPasswPresenter(identifier: StorageIdentifier): GenPasswPresenter

    fun genHistPresenter(identifier: StorageIdentifier): GenHistPresenter

    fun noteHistPresenter(noteIdentifier: NoteIdentifier): NoteHistPresenter

    fun notePresenter(noteIdentifier: NoteIdentifier): NotePresenter

    fun otpNotePresenter(noteIdentifier: NoteIdentifier): OtpNotePresenter

    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter

    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter

    fun settingsPresenter(): SettingsPresenter

    fun pluginsPresenter(): PluginsPresenter

    fun pluginPresenter(identifier: DynamicFeature): PluginPresenter

}