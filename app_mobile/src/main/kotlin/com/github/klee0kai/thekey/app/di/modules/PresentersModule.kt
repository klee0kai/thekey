package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.ui.changepassw.presenter.ChangeStoragePasswordPresenter
import com.github.klee0kai.thekey.app.ui.changepassw.presenter.ChangeStoragePasswordPresenterImpl
import com.github.klee0kai.thekey.app.ui.editstorage.presenter.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.editstorage.presenter.EditStoragePresenterImpl
import com.github.klee0kai.thekey.app.ui.hist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.hist.presenter.GenPasswHistPresenterImpl
import com.github.klee0kai.thekey.app.ui.hist.presenter.NoteHistPresenter
import com.github.klee0kai.thekey.app.ui.hist.presenter.NotePasswHistPresenterImpl
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenterImpl
import com.github.klee0kai.thekey.app.ui.main.presenter.MainPresenter
import com.github.klee0kai.thekey.app.ui.main.presenter.MainPresenterImpl
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenter
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterImpl
import com.github.klee0kai.thekey.app.ui.note.presenter.NotePresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.NotePresenterImpl
import com.github.klee0kai.thekey.app.ui.noteedit.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.noteedit.presenter.EditNotePresenterImpl
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenter
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterImpl
import com.github.klee0kai.thekey.app.ui.otpnote.presenter.OtpNotePresenter
import com.github.klee0kai.thekey.app.ui.otpnote.presenter.OtpNotePresenterImpl
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenterImpl
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenterImpl
import com.github.klee0kai.thekey.app.ui.settings.presenter.SettingsPresenter
import com.github.klee0kai.thekey.app.ui.settings.presenter.SettingsPresenterImpl
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenterImpl
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterImpl
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupPresenter
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupsPresenterImpl
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterImpl
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

@Module
interface PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun mainPresenter(): MainPresenter = MainPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun loginPresenter(storageIdentifier: StorageIdentifier): LoginPresenter =
        LoginPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun navigationBoardPresenter(): NavigationBoardPresenter = NavigationBoardPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun storagesPresenter(): StoragesPresenter = StoragesPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun editStoragePresenter(storageIdentifier: StorageIdentifier?): EditStoragePresenter =
        EditStoragePresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun changeStoragePasswordPresenter(storageIdentifier: StorageIdentifier): ChangeStoragePasswordPresenter =
        ChangeStoragePasswordPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun storagePresenter(storageIdentifier: StorageIdentifier): StoragePresenter =
        StoragePresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier): EditStoragesGroupPresenter =
        EditStoragesGroupsPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun genPasswPresenter(storageIdentifier: StorageIdentifier): GenPasswPresenter =
        GenPasswPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun genHistPresenter(storageIdentifier: StorageIdentifier): GenHistPresenter =
        GenPasswHistPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun noteHistPresenter(noteIdentifier: NoteIdentifier): NoteHistPresenter =
        NotePasswHistPresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun notePresenter(noteIdentifier: NoteIdentifier): NotePresenter =
        NotePresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun otpNotePresenter(noteIdentifier: NoteIdentifier): OtpNotePresenter =
        OtpNotePresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
        EditNotePresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter =
        EditNoteGroupsPresenterImpl(id)

    @Provide(cache = Provide.CacheType.Weak)
    fun settingsPresenter(): SettingsPresenter = SettingsPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun pluginsPresenter(): PluginsPresenter = PluginsPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun pluginPresenter(feature: DynamicFeature): PluginPresenter = PluginPresenterImpl(feature)

}