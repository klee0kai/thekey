package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GenPasswRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)
    val scope = DI.defaultThreadScope()

    private val _allHistPasswList = MutableStateFlow<List<HistPassw>>(emptyList())
    val allHistPasswList = flow {
        loadHistory()
        _allHistPasswList.collect(this)
    }

    suspend fun lastGeneratedPassw(): String {
        return engine().lastGeneratedPassw()
    }

    suspend fun generateNewPassw(params: GenPasswParams): String {
        val res = engine().generateNewPassw(params)
        loadHistory(force = true, ifNotEmptyOnly = true)
        return res
    }

    suspend fun removeHist(histPtr: Long) = coroutineScope {
        val fakeRemove = launch {
            _allHistPasswList.update { list -> list.filter { it.histPtr != histPtr } }
        }

        engine().removeHist(histPtr)
        fakeRemove.join()
        loadHistory(force = true, ifNotEmptyOnly = true)
    }

    suspend fun cleanOld(cleanTime: Long) {
        val cleanTimeSec = TimeUnit.MILLISECONDS.toSeconds(cleanTime)
        engine().removeOldHist(cleanTimeSec)
        loadHistory(force = true, ifNotEmptyOnly = true)
    }

    private fun loadHistory(
        force: Boolean = false,
        ifNotEmptyOnly: Boolean = false,
    ) = scope.launch {
        if (_allHistPasswList.value.isNotEmpty() && !force) return@launch
        if (_allHistPasswList.value.isEmpty() && ifNotEmptyOnly) return@launch

        if (_allHistPasswList.value.isEmpty()) {
            _allHistPasswList.value = engine().genHistory()
                .reversed()
                .map { hist: DecryptedPassw -> hist.histPasww() }
        }

        _allHistPasswList.value = engine().genHistory(info = true)
            .reversed()
            .map { hist: DecryptedPassw -> hist.histPasww(isLoaded = true) }
    }

}