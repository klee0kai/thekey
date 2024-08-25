package com.github.klee0kai.thekey.app.ui.settings.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.settings.SettingState
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class SettingsPresenterImpl : SettingsPresenter {

    private val scope = DI.defaultThreadScope()
    private val settings = DI.settingsRepositoryLazy()
    private val billing = DI.billingInteractor()

    private val _state = MutableStateFlow<SettingState?>(null)
    override val state = _state.filterNotNull()

    override fun init() = scope.launch {
        _state.value = SettingState(
            analytics = settings().analytics(),
            loginSecure = settings().loginSecure(),
            encryptionComplexity = settings().encryptionComplexity(),
            histPeriod = settings().histPeriod(),
        ).resetPaidFeatures()
    }

    override fun input(
        block: SettingState.() -> SettingState,
    ) = scope.launch {
        val old = _state.value
        val newState = old?.let(block)
            ?.resetPaidFeatures()
        _state.value = newState

        if (newState?.analytics != null && newState.analytics != old.analytics) {
            settings().analytics.set(newState.analytics)
        }
        if (newState?.loginSecure != null && newState.loginSecure != old.loginSecure) {
            settings().loginSecure.set(newState.loginSecure)
        }
        if (newState?.encryptionComplexity != null && newState.encryptionComplexity != old.encryptionComplexity) {
            settings().encryptionComplexity.set(newState.encryptionComplexity)
        }
        if (newState?.histPeriod != null && newState.histPeriod != old.histPeriod) {
            settings().histPeriod.set(newState.histPeriod)
        }
    }


    private fun SettingState.resetPaidFeatures(): SettingState {
        var state = this
        if (!billing.isAvailable(PaidFeature.UNLIMITED_HIST_PERIOD)) {
            state = state.copy(
                histPeriod = when (state.histPeriod) {
                    null, HistPeriod.SHORT, HistPeriod.NORMAL -> state.histPeriod
                    HistPeriod.LONG, HistPeriod.VERY_LONG -> HistPeriod.SHORT
                }
            )
        }
        return state
    }

}