package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.data.repositories.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.settings.SettingsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.LoginnedStorages
import com.github.klee0kai.thekey.app.data.repositories.storage.NotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.OtpNotesRepository
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier

@Module
interface RepositoriesModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun foundStoragesRepository(): FoundStoragesRepository

    @Provide(cache = Provide.CacheType.Soft)
    fun settingsRepository(): SettingsRepository

    @Provide(cache = Provide.CacheType.Strong)
    fun loginnedStorages(): LoginnedStorages

    @Provide(cache = Provide.CacheType.Soft)
    fun notesRepository(storageIdentifier: StorageIdentifier): NotesRepository

    @Provide(cache = Provide.CacheType.Soft)
    fun otpNotesRepository(storageIdentifier: StorageIdentifier): OtpNotesRepository

    @Provide(cache = Provide.CacheType.Soft)
    fun groupRepository(storageIdentifier: StorageIdentifier): GroupsRepository

}