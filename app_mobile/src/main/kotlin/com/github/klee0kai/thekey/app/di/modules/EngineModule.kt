package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.engine.FindStorageEngine

@Module
interface EngineModule {

    @Provide(cache = Provide.CacheType.Strong)
    fun finStoragesEngine(): FindStorageEngine

}