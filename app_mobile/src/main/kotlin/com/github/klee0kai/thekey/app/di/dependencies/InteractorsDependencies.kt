package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.wrappers.AsyncProvide
import com.github.klee0kai.thekey.app.domain.FindStoragesInteractor

interface InteractorsDependencies {

    fun findStoragesInteractorLazy(): AsyncProvide<FindStoragesInteractor>

}