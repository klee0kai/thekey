package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.data.model.AuthorizedStorage
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AuthorizedStoragesRepository {

    private val authorizedStoragesSet = MutableStateFlow<Set<AuthorizedStorage>>(emptySet())
    val authorizedStorages = authorizedStoragesSet.map { set ->
        set.sortedByDescending { it.loginTime }
            .map { it.identifier }
    }

    fun auth(storage: StorageIdentifier) {
        authorizedStoragesSet.update { set ->
            (set - AuthorizedStorage(storage)) + AuthorizedStorage(storage)
        }
    }

    fun logout(storage: StorageIdentifier) {
        authorizedStoragesSet.update { set -> set - AuthorizedStorage(storage) }
    }

    fun logoutAll() {
        authorizedStoragesSet.value = emptySet()
    }

}