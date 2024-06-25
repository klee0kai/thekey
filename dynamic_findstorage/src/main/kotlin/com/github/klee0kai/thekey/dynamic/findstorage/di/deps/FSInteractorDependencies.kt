package com.github.klee0kai.thekey.dynamic.findstorage.di.deps

import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.dynamic.findstorage.domain.FindStorageInteractor

interface FSInteractorDependencies {

    fun findStoragesInteractorLazy(): AsyncCoroutineProvide<FindStorageInteractor>

}