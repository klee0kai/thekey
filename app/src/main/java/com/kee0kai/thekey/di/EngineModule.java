package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.Module;
import com.github.klee0kai.stone.annotations.module.Provide;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.FindStorageEngine;

@Module
public interface EngineModule {

    @Provide
    public CryptStorageEngine cryptEngine();

    @Provide
    public FindStorageEngine findStorageEngine();

}
