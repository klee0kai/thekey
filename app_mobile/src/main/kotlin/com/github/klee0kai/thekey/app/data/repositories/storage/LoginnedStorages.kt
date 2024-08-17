package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class LoginnedStorages {

    val logginedStorages = MutableStateFlow<Set<StorageIdentifier>>(emptySet())

    fun logined(storage: StorageIdentifier) {
        logginedStorages.update { set -> set + storage }
    }

    fun logouted(storage: StorageIdentifier) {
        logginedStorages.update { set -> set - storage }
    }

}