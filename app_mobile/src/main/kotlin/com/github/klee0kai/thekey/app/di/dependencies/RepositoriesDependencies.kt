package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.data.repositories.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.settings.SettingsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.LoginnedStorages
import com.github.klee0kai.thekey.app.data.repositories.storage.NotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.OtpNotesRepository
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface RepositoriesDependencies {

    fun foundStoragesRepositoryLazy(): AsyncCoroutineProvide<FoundStoragesRepository>

    fun settingsRepositoryLazy(): AsyncCoroutineProvide<SettingsRepository>

    fun loginedRepLazy(): AsyncCoroutineProvide<LoginnedStorages>

    fun notesRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<NotesRepository>

    fun otpNotesRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<OtpNotesRepository>

    fun groupRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GroupsRepository>

}