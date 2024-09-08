package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.domain.EditStorageInteractor
import com.github.klee0kai.thekey.app.domain.GenPasswInteractor
import com.github.klee0kai.thekey.app.domain.GroupsInteractor
import com.github.klee0kai.thekey.app.domain.LoginInteractor
import com.github.klee0kai.thekey.app.domain.NotesInteractor
import com.github.klee0kai.thekey.app.domain.OtpNotesInteractor
import com.github.klee0kai.thekey.app.domain.PredefinedNoteGroupsInteractor
import com.github.klee0kai.thekey.app.domain.StoragesInteractor
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface InteractorsDependencies {

    fun loginInteractorLazy(): AsyncCoroutineProvide<LoginInteractor>

    fun storagesInteractorLazy(): AsyncCoroutineProvide<StoragesInteractor>

    fun editStorageInteractorLazy(): AsyncCoroutineProvide<EditStorageInteractor>

    fun notesInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<NotesInteractor>

    fun otpNotesInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<OtpNotesInteractor>

    fun groupsInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GroupsInteractor>

    fun predefinedNoteGroupsInteractor(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<PredefinedNoteGroupsInteractor>

    fun genPasswInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GenPasswInteractor>

}