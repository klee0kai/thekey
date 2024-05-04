package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
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
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenterImpl
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenterImpl
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenterImpl
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterImpl
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

@Module
interface PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    open fun loginPresenter(): LoginPresenter = LoginPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun navigationBoardPresenter(): NavigationBoardPresenter = NavigationBoardPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun storagesPresenter(): StoragesPresenter = StoragesPresenter()

    @Provide(cache = Provide.CacheType.Weak)
    fun editStoragePresenter(storageIdentifier: StorageIdentifier?): CreateStoragePresenter {
        return if (storageIdentifier?.path == null) {
            CreateStoragePresenter()
        } else {
            EditStoragePresenter(storageIdentifier.path)
        }
    }

    @Provide(cache = Provide.CacheType.Weak)
    fun storagePresenter(storageIdentifier: StorageIdentifier): StoragePresenter =
        StoragePresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun genPasswPresente(storageIdentifier: StorageIdentifier): GenPasswPresenter =
        GenPasswPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun genHistPresenter(storageIdentifier: StorageIdentifier): GenHistPresenter =
        GenHistPresenterImpl(storageIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
        EditNotePresenterImpl(noteIdentifier)

    @Provide(cache = Provide.CacheType.Weak)
    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupsPresenter =
        EditNoteGroupsPresenterImpl(id)

    @Provide(cache = Provide.CacheType.Weak)
    fun pluginsPresenter(): PluginsPresenter = PluginsPresenterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun pluginPresenter(feature: DynamicFeature): PluginPresenter = PluginPresenterImpl(feature)

}