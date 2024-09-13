package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.data.repositories.storage.AuthorizedStoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GenPasswRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.NotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.OtpNotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.PredefinedNoteGroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storages.StoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.storages.StoragesRepositoryImpl
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier

@Module
interface RepositoriesModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun storagesRepository(): StoragesRepository = StoragesRepositoryImpl()

    @Provide(cache = Provide.CacheType.Strong)
    fun authorizedStorages(): AuthorizedStoragesRepository

    @Provide(cache = Provide.CacheType.Weak)
    fun notesRepository(storageIdentifier: StorageIdentifier): NotesRepository

    @Provide(cache = Provide.CacheType.Weak)
    fun otpNotesRepository(storageIdentifier: StorageIdentifier): OtpNotesRepository

    @Provide(cache = Provide.CacheType.Weak)
    fun groupRepository(storageIdentifier: StorageIdentifier): GroupsRepository

    @Provide(cache = Provide.CacheType.Weak)
    fun predefinedNoteGroupsRepository(storageIdentifier: StorageIdentifier): PredefinedNoteGroupsRepository

    @Provide(cache = Provide.CacheType.Weak)
    fun genHistoryRepository(storageIdentifier: StorageIdentifier): GenPasswRepository

}