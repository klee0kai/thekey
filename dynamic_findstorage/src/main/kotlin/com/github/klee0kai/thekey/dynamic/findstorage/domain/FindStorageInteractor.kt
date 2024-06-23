package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.findstorage.findStoragesFlow
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.core.utils.common.launchIfNotStarted
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.perm.writeStoragePermissions
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class FindStorageInteractor {

    private val scope = FSDI.defaultThreadScope()
    private val perm = FSDI.permissionsHelper()
    private val engine = FSDI.findStorageEngineSaveLazy()
    private val rep = FSDI.storagesRepositoryLazy()
    private val settings = FSDI.fsSettingsRepositoryLazy()
    val searchState = MutableStateFlow(false)

    fun findStoragesIfNeed(force: Boolean = false) = scope.launchIfNotStarted("search_storages") {
        val canToScan = perm.checkPermissions(perm.writeStoragePermissions())
        val needToScan = AsyncCoroutineProvide { checkForceFind(force = force) }
        if (!canToScan || !needToScan()) return@launchIfNotStarted
        searchState.value = true

        DI.userShortPaths().rootAbsolutePaths
            .map { root ->
                engine().findStoragesFlow(root)
                    .collect { storage ->
                        if (rep().findStorage(storage.path).await() == null) {
                            rep().setStorage(storage.toColoredStorage())
                        }
                    }
            }

        searchState.value = false
    }


    private suspend fun checkForceFind(force: Boolean = false): Boolean {
        val curTime = System.currentTimeMillis()
        val lastStartTime = settings().lastSearchTime.get().await()
        if (!force && curTime - lastStartTime < RESTART_TIME) {
            return false
        }
        settings().lastSearchTime.set(curTime)
        return true
    }

    companion object {
        private val RESTART_TIME = TimeUnit.MINUTES.toMillis(10)
    }

}