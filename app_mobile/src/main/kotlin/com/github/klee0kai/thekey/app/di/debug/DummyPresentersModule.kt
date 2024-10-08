package com.github.klee0kai.thekey.app.di.debug

import com.github.klee0kai.thekey.app.di.modules.PresentersModule
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

open class DummyPresentersModule(
    origin: PresentersModule,
) : PresentersModule by origin {

    override fun mainPresenter() = object : MainPresenter {}

    override fun loginPresenter(storageIdentifier: StorageIdentifier) = object : LoginPresenter {}

    override fun simpleBoardPresenter() = object : SimpleBoardPresenter {}

    override fun storagesPresenter() = object : StoragesPresenter {}

    override fun editStoragePresenter(
        storageIdentifier: StorageIdentifier?,
    ) = object : EditStoragePresenter {}

    override fun changeStoragePasswordPresenter(
        storageIdentifier: StorageIdentifier,
    ) = object : ChangeStoragePasswordPresenter {}


    override fun storagePresenter(
        storageIdentifier: StorageIdentifier,
    ) = object : StoragePresenter {}

    override fun editStorageGroupPresenter(
        storageIdentifier: StorageGroupIdentifier,
    ) = object : EditStoragesGroupPresenter {}

    override fun genPasswPresenter(
        storageIdentifier: StorageIdentifier,
    ) = object : GenPasswPresenter {}

    override fun genHistPresenter(
        storageIdentifier: StorageIdentifier,
    ) = object : GenHistPresenter {}

    override fun noteHistPresenter(
        noteIdentifier: NoteIdentifier
    ) = object : NoteHistPresenter {}

    override fun notePresenter(
        noteIdentifier: NoteIdentifier,
    ) = object : NotePresenter {}

    override fun otpNotePresenter(
        noteIdentifier: NoteIdentifier,
    ) = object : OtpNotePresenter {}

    override fun editNotePresenter(
        noteIdentifier: NoteIdentifier,
    ) = object : EditNotePresenter {}

    override fun editNoteGroupPresenter(
        id: NoteGroupIdentifier,
    ) = object : EditNoteGroupsPresenter {}

    override fun settingsPresenter() = object : SettingsPresenter {}

    override fun pluginsPresenter() = object : PluginsPresenter {}

    override fun pluginPresenter(
        feature: DynamicFeature,
    ) = object : PluginPresenter {}


}