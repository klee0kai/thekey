package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.FindStorageEngine;

@Module
public class EngineModule {

    @Singleton
    public CryptStorageEngine cryptEngine() {
        return new CryptStorageEngine();
    }

    @Singleton
    public FindStorageEngine findStorageEngine() {
        return new FindStorageEngine();
    }

}
