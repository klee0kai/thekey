package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.domain.FindStoragesInteractor
import com.github.klee0kai.thekey.app.domain.LoginInteractor

interface InteractorsDependencies {

    fun findStoragesInteractorLazy(): AsyncCoroutineProvide<FindStoragesInteractor>

    fun loginInteractorLazy(): AsyncCoroutineProvide<LoginInteractor>

}