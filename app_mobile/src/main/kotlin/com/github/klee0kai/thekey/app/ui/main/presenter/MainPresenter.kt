package com.github.klee0kai.thekey.app.ui.main.presenter

import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface MainPresenter {

    val loginSecureMode: Flow<LoginSecureMode> get() = emptyFlow()

}