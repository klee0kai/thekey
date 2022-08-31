package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.domain.room.KeyDatabase;

@Module
public class ProviderModule {

    @Singleton(cache = Singleton.CacheType.STRONG)
    public KeyDatabase keyDatabase() {
        return KeyDatabase.create();
    }

}
