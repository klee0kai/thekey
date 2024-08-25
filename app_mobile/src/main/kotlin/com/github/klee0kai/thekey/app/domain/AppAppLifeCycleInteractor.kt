package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes

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
            val logoutTimeout = when (settings().loginSecure()) {
                LoginSecureMode.LOW_SECURE -> 10.minutes
                LoginSecureMode.MIDDLE_SECURE -> 1.minutes
                LoginSecureMode.HARD_SECURE -> ZERO
            }
            delay(logoutTimeout)
            loginInteractor().logoutAll().join()
        }
    }
}