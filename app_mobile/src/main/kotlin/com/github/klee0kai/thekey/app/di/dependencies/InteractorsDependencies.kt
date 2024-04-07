package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.domain.FindStoragesInteractor
import com.github.klee0kai.thekey.app.domain.GroupsInteractor
import com.github.klee0kai.thekey.app.domain.LoginInteractor
import com.github.klee0kai.thekey.app.domain.NotesInteractor

interface InteractorsDependencies {

    fun findStoragesInteractorLazy(): AsyncCoroutineProvide<FindStoragesInteractor>

    fun loginInteractorLazy(): AsyncCoroutineProvide<LoginInteractor>

    fun notesInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<NotesInteractor>

    fun groupsInteractorLazy(storageIdentifier: StorageIdentifier): AsyncCoroutineProvide<GroupsInteractor>

}