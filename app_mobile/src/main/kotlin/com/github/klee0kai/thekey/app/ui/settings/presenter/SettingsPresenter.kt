package com.github.klee0kai.thekey.app.ui.settings.presenter

import com.github.klee0kai.thekey.app.ui.settings.SettingState
import com.github.klee0kai.thekey.core.utils.common.Initializable
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface SettingsPresenter : Initializable {

    val state: Flow<SettingState> get() = emptyFlow()

    fun input(block: SettingState.() -> SettingState): Job = emptyJob()

}