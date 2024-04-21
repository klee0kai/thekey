package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.editstorage.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenterImpl
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenterImpl
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenter
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterImpl
import com.github.klee0kai.thekey.app.ui.note.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.EditNotePresenterImpl
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenter
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterImpl
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenterImpl
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterImpl
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

@Module
open class PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    open fun loginPresenter(): LoginPresenter = LoginPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    open fun navigationBoardPresenter(): NavigationBoardPresenter = NavigationBoardPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    open fun storagesPresenter(): StoragesPresenter = StoragesPresenter()

    @Provide(cache = Provide.CacheType.Weak)
    open fun editStoragePresenter(storageIdentifier: StorageIdentifier?): CreateStoragePresenter {
        return if (storageIdentifier?.path == null) {
            CreateStoragePresenter()
        } else {
            EditStoragePresenter(storageIdentifier.path)
        }
    }

    @Provide(cache = Provide.CacheType.Weak)
    open fun storagePresenter(storageIdentifier: StorageIdentifier): StoragePresenter =
        StoragePresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    open fun genPasswPresente(storageIdentifier: StorageIdentifier): GenPasswPresenter =
        GenPasswPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    open fun genHistPresenter(storageIdentifier: StorageIdentifier): GenHistPresenter =
        GenHistPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    open fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
        EditNotePresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    open fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter =
        EditNoteGroupsPresenterImpl(id)

    @Provide(cache = Provide.CacheType.Weak)
    open fun pluginsPresenter(): PluginsPresenter = PluginsPresenter()

    @Provide(cache = Provide.CacheType.Weak)
    open fun pluginPresenter(identifier: PluginIdentifier): PluginPresenter = PluginPresenter(identifier)

}