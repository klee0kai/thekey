package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.findStorages
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.utils.android.UserShortPaths
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FindStoragesInteractor {

    val storagesFlow = callbackFlow<List<Storage>> {
        launch {
            rep.updateDbFlow.collect {
                send(rep.getStorages().await())
            }
        }
        send(rep.getStorages().await())
        awaitClose()
    }.distinctUntilChanged()

    private val scope = DI.ioThreadScope()
    private val engine by DI.findStorageEngineLazy()
    private val rep by DI.foundStoragesRepositoryLazy()
    private val settingsRep by DI.settingsRepositoryLazy()

    private var lastStartTime: Long = 0

    fun findStorages(force: Boolean = false) = scope.launch {
        if (!checkForceFind(force = force)) return@launch
        val searchingJobs = UserShortPaths.getRootPaths(false)
            .map { root ->
                launch {
                    engine.findStorages(root)
                        .collect { storage ->
                            rep.addStorage(storage = storage).join()
                        }
                }
            }
        searchingJobs.forEach { it.join() }
    }

    private fun checkForceFind(force: Boolean = false): Boolean {
        val curTime = System.currentTimeMillis()
        if (!force && curTime - lastStartTime < RESTART_TIME) {
            return false
        }
        lastStartTime = curTime
        return true
    }

    companion object {
        private val RESTART_TIME = TimeUnit.MINUTES.toMillis(10)
    }
}