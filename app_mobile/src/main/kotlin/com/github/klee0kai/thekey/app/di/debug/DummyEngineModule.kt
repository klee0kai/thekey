package com.github.klee0kai.thekey.app.di.debug

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.modules.EngineModule
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageDummyEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageSuspended

@Module
abstract class DummyEngineModule : EngineModule() {

    @Provide(cache = Provide.CacheType.Strong)
    override fun findStoragesSuspend(): FindStorageSuspended = FindStorageDummyEngine()
    
}