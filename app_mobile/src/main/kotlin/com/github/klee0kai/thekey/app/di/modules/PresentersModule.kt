package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.editstorage.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.genhist.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter
import com.github.klee0kai.thekey.app.ui.note.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupPresenter
import com.github.klee0kai.thekey.app.ui.storage.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

@Module
abstract class PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun loginPresenter(): LoginPresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun storagesPresenter(): StoragesPresenter

    @Provide(cache = Provide.CacheType.Weak)
    open fun editStoragePresenter(storageIdentifier: StorageIdentifier?): CreateStoragePresenter {
        return if (storageIdentifier?.path == null) {
            CreateStoragePresenter()
        } else {
            EditStoragePresenter(storageIdentifier.path)
        }
    }

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun storagePresenter(storageIdentifier: StorageIdentifier): StoragePresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun genPasswPresente(storageIdentifier: StorageIdentifier): GenPasswPresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun genHistPresenter(storageIdentifier: StorageIdentifier): GenHistPresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupPresenter


}