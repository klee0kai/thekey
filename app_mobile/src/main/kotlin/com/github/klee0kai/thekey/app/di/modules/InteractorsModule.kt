package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.FindStoragesInteractor
import com.github.klee0kai.thekey.app.domain.GroupsInteractor
import com.github.klee0kai.thekey.app.domain.LoginInteractor
import com.github.klee0kai.thekey.app.domain.NotesInteractor

@Module
interface InteractorsModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun findStoragesInteractor(): FindStoragesInteractor

    @Provide(cache = Provide.CacheType.Soft)
    fun loginInteractor(): LoginInteractor

    @Provide(cache = Provide.CacheType.Soft)
    fun notesInteractor(storageIdentifier: StorageIdentifier): NotesInteractor

    @Provide(cache = Provide.CacheType.Soft)
    fun groupsInteractor(storageIdentifier: StorageIdentifier): GroupsInteractor

}