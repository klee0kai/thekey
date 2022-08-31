package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.ChangeableSingleton;
import com.github.klee0kai.stone.annotations.Module;
import com.kee0kai.thekey.App;

@Module
public interface AppModule {

    @ChangeableSingleton
    App application();


}
