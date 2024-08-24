package com.github.klee0kai.thekey.app.ui.settings.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.settings.SettingState
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

class SettingsPresenterImpl : SettingsPresenter {

    private val scope = DI.defaultThreadScope()
    private val settings = DI.settingsRepositoryLazy()

    private val _state = MutableStateFlow<SettingState?>(null)
    override val state = _state.filterNotNull()

    override fun init() {
        scope.launch {
            _state.value = SettingState(
                autoSearch = settings().storageAutoSearch(),
                loginSecure = settings().loginSecure(),
                encryptionComplexity = settings().encryptionComplexity(),
            )
        }
    }

    override fun input(block: SettingState.() -> SettingState) = scope.launch {
        _state.update { old -> old?.let(block) }
    }

}