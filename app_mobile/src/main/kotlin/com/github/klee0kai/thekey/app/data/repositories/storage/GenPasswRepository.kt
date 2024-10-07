package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

class GenPasswRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    private val _allHistPasswList = lazyStateFlow(
        init = null as? List<HistPassw>?,
        defaultArg = false,
        scope = scope,
    ) { force ->
        if (!value.isNullOrEmpty() && !force) return@lazyStateFlow

        if (value.isNullOrEmpty()) {
            value = engine().genHistory()
                .reversed()
                .map { hist: DecryptedPassw -> hist.histPasww() }
        }

        value = engine().genHistory(info = true)
            .reversed()
            .map { hist: DecryptedPassw -> hist.histPasww(isLoaded = true) }
    }

    val allHistPasswList = _allHistPasswList.filterNotNull()


    suspend fun lastGeneratedPassw(): String {
        return engine().lastGeneratedPassw()
    }

    suspend fun generateNewPassw(params: GenPasswParams): String {
        val res = engine().generateNewPassw(params)
        _allHistPasswList.touch(true)
        return res
    }

    suspend fun removeHist(histPtr: Long) = coroutineScope {
        if (DebugConfigs.isNotesFastUpdate) {
            _allHistPasswList.update { list -> list?.filter { it.id != histPtr } }
        }

        engine().removeHist(histPtr)
        _allHistPasswList.touch(true)
    }

    suspend fun cleanOld(cleanTime: Long) {
        val cleanTimeSec = TimeUnit.MILLISECONDS.toSeconds(cleanTime)
        engine().removeOldHist(cleanTimeSec)
        _allHistPasswList.touch(true)
    }

    suspend fun clearCache() {
        _allHistPasswList.update { null }
    }


}