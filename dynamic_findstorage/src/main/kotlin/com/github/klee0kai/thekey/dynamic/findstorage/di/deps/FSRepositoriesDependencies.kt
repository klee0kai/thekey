package com.github.klee0kai.thekey.dynamic.findstorage.di.deps

import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.dynamic.findstorage.data.FSSettingsRepository

interface FSRepositoriesDependencies {

    fun fsSettingsRepositoryLazy(): AsyncCoroutineProvide<FSSettingsRepository>

}