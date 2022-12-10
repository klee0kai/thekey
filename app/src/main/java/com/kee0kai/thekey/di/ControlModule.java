package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.Module;
import com.github.klee0kai.stone.annotations.module.Provide;
import com.kee0kai.thekey.managers.ActivitySecureManager;
import com.kee0kai.thekey.navig.InnerNavigator;

@Module
public interface ControlModule {

    @Provide
    public InnerNavigator innerNavigator();

    @Provide(cache = Provide.CacheType.Strong)
    public ActivitySecureManager activitySecureManager();


}
