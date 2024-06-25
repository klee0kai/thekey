package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.domain.EditStorageInteractor
import com.github.klee0kai.thekey.app.domain.GroupsInteractor
import com.github.klee0kai.thekey.app.domain.LoginInteractor
import com.github.klee0kai.thekey.app.domain.NotesInteractor
import com.github.klee0kai.thekey.app.domain.OtpNotesInteractor
import com.github.klee0kai.thekey.app.domain.StoragesInteractor
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier

@Module
interface InteractorsModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun loginInteractor(): LoginInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun storagesInteractor(): StoragesInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun editStorageInteractor(): EditStorageInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun notesInteractor(storageIdentifier: StorageIdentifier): NotesInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun otpNotesInteractor(storageIdentifier: StorageIdentifier): OtpNotesInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun groupsInteractor(storageIdentifier: StorageIdentifier): GroupsInteractor

}