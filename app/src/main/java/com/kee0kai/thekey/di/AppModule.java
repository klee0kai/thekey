package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.BindInstance;
import com.github.klee0kai.stone.annotations.module.Module;
import com.kee0kai.thekey.App;

@Module
public interface AppModule {

    @BindInstance
    App application();


}
