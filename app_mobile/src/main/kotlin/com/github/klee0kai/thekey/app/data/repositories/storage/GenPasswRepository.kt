package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.utils.coroutine.collectTo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class GenPasswRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    private val consumers = AtomicInteger(0)
    private val _allHistPasswList = MutableStateFlow<List<HistPassw>>(emptyList())
    val allHistPasswList = channelFlow {
        consumers.incrementAndGet()
        loadHistory()
        _allHistPasswList.collectTo(this)
        awaitClose { consumers.decrementAndGet() }
    }

    suspend fun lastGeneratedPassw(): String {
        return engine().lastGeneratedPassw()
    }

    suspend fun generateNewPassw(params: GenPasswParams): String {
        val res = engine().generateNewPassw(params)
        loadHistory(force = true)
        return res
    }

    suspend fun removeHist(histPtr: Long) = coroutineScope {
        val fakeRemove = launch {
            _allHistPasswList.update { list -> list.filter { it.histPtr != histPtr } }
        }

        engine().removeHist(histPtr)
        fakeRemove.join()
        loadHistory(force = true)
    }

    suspend fun cleanOld(cleanTime: Long) {
        val cleanTimeSec = TimeUnit.MILLISECONDS.toSeconds(cleanTime)
        engine().removeOldHist(cleanTimeSec)
        loadHistory(force = true)
    }

    suspend fun clearCache() {
        _allHistPasswList.update { emptyList() }
    }

    private fun loadHistory(
        force: Boolean = false,
    ) = scope.launch {
        if (_allHistPasswList.value.isNotEmpty() && !force) return@launch
        if (consumers.get() <= 0) {
            // no consumers
            _allHistPasswList.value = emptyList()
            return@launch
        }

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