package com.github.klee0kai.thekey.dynamic.findstorage.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.dynamic.findstorage.data.FSSettingsRepository

@Module
interface FSRepositoriesModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun fSSettingsRepository(): FSSettingsRepository

}