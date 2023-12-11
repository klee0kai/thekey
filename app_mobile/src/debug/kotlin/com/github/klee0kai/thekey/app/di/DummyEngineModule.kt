package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.modules.EngineModule
import com.github.klee0kai.thekey.app.engine.FindStorageDummyEngine

@Module
interface DummyEngineModule : EngineModule {

    @Provide(cache = Provide.CacheType.Strong)
    override fun findStoragesEngine(): FindStorageDummyEngine

}