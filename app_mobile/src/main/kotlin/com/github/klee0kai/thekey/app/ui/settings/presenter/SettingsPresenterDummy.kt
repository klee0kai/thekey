package com.github.klee0kai.thekey.app.ui.settings.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.settings.SettingState
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class SettingsPresenterDummy : SettingsPresenter {

    private val scope = DI.defaultThreadScope()

    override val state = MutableStateFlow(
        SettingState(
            analytics = false,
            loginSecure = LoginSecureMode.LOW_SECURE,
            encryptionComplexity = NewStorageSecureMode.LOW_SECURE,
            histPeriod = HistPeriod.SHORT,
        )
    )

    override fun input(block: SettingState.() -> SettingState) =
        scope.launch {
            state.update { block.invoke(it) }
        }

}