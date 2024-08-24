package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class AppAppLifeCycleInteractor(
    val origin: AppLifeCycleInteractor,
) : AppLifeCycleInteractor by origin {

    private val scope = DI.defaultThreadScope()
    private val loginInteractor = DI.loginInteractorLazy()
    private val settings = DI.settingsRepositoryLazy()
    private var unloginJob: Job? = null

    override fun appResumed() {
        super.appResumed()
        unloginJob?.cancel()
        unloginJob = null
    }

    override fun appMinimazed() {
        super.appMinimazed()
        unloginJob = scope.launch {
            delay(settings().logoutTimeout())
            loginInteractor().logoutAll().join()
        }
    }
}