package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.Module;
import com.github.klee0kai.stone.annotations.module.Provide;
import com.kee0kai.thekey.domain.room.KeyDatabase;

@Module
public class ProviderModule {

    @Provide(cache = Provide.CacheType.Strong)
    public KeyDatabase keyDatabase() {
        return KeyDatabase.create();
    }

}
