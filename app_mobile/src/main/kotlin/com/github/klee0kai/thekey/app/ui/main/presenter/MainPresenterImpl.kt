package com.github.klee0kai.thekey.app.ui.main.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MainPresenterImpl : MainPresenter {

    private val scope = DI.defaultThreadScope()
    private val settings = DI.settingsRepositoryLazy()

    override val loginSecureMode = flow {
        settings().loginSecure.flow.collect(this)
    }.stateIn(scope, SharingStarted.Eagerly, LoginSecureMode.LOW_SECURE)

    override val isMakeBlur = MutableStateFlow(false)

    override fun windowFocus(focus: Boolean) {
        super.windowFocus(focus)
        scope.launch {
            when {
                focus || loginSecureMode.value == LoginSecureMode.LOW_SECURE -> {
                    isMakeBlur.value = false
                }

                loginSecureMode.value in arrayOf(
                    LoginSecureMode.MIDDLE_SECURE,
                    LoginSecureMode.HARD_SECURE
                ) -> {
                    isMakeBlur.value = true
                }
            }
        }
    }

}