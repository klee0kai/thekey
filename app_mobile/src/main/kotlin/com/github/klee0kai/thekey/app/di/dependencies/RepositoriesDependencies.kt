package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.data.repositories.storage.AuthorizedStoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GenPasswRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.GroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.NotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.OtpNotesRepository
import com.github.klee0kai.thekey.app.data.repositories.storage.PredefinedNoteGroupsRepository
import com.github.klee0kai.thekey.app.data.repositories.storages.StoragesRepository
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface RepositoriesDependencies {

    fun storagesRepositoryLazy(): AsyncCoroutineProvide<StoragesRepository>

    fun authorizedRepLazy(): AsyncCoroutineProvide<AuthorizedStoragesRepository>

    fun notesRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<NotesRepository>

    fun otpNotesRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<OtpNotesRepository>

    fun groupRepLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GroupsRepository>

    fun predefinedNoteGroupsRepository(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<PredefinedNoteGroupsRepository>

    fun genHistoryRepositoryLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GenPasswRepository>

}