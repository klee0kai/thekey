package com.github.klee0kai.thekey.app.ui.genhist.presenter

import com.github.klee0kai.thekey.core.domain.model.HistPassw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface GenHistPresenter {

    val histFlow: Flow<List<HistPassw>> get() = emptyFlow()

}