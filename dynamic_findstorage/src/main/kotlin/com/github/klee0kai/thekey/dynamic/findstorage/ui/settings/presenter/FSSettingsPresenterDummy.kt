package com.github.klee0kai.thekey.dynamic.findstorage.ui.settings.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class FSSettingsPresenterDummy : FSSettingsPresenter {

    private val scope = DI.defaultThreadScope()

    override val isAutoSearchEnable = MutableStateFlow(false)

    override fun toggleAutoSearch(appRouter: AppRouter?) = scope.launch {
        isAutoSearchEnable.update { !it }
    }

}