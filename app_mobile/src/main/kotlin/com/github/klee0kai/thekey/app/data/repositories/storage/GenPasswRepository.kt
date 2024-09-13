package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.utils.coroutine.collectTo
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
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

    val allHistPasswList = lazyStateFlow(
        init = emptyList<HistPassw>(),
        defaultArg = false,
        scope = scope,
    ) { force ->
        if (value.isNotEmpty() && !force) return@lazyStateFlow

        if (value.isEmpty()) {
            value = engine().genHistory()
                .reversed()
                .map { hist: DecryptedPassw -> hist.histPasww() }
        }

        value = engine().genHistory(info = true)
            .reversed()
            .map { hist: DecryptedPassw -> hist.histPasww(isLoaded = true) }
    }

    suspend fun lastGeneratedPassw(): String {
        return engine().lastGeneratedPassw()
    }

    suspend fun generateNewPassw(params: GenPasswParams): String {
        val res = engine().generateNewPassw(params)
        allHistPasswList.touch(true)
        return res
    }

    suspend fun removeHist(histPtr: Long) = coroutineScope {
        if (DebugConfigs.isNotesFastUpdate) {
            allHistPasswList.update { list -> list.filter { it.id != histPtr } }
        }

        engine().removeHist(histPtr)
        allHistPasswList.touch(true)
    }

    suspend fun cleanOld(cleanTime: Long) {
        val cleanTimeSec = TimeUnit.MILLISECONDS.toSeconds(cleanTime)
        engine().removeOldHist(cleanTimeSec)
        allHistPasswList.touch(true)
    }

    suspend fun clearCache() {
        allHistPasswList.update { emptyList() }
    }


}