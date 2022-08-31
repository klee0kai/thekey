package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.managers.ActivitySecureManager;
import com.kee0kai.thekey.navig.InnerNavigator;

@Module
public class ControlModule {

    @Singleton
    public InnerNavigator innerNavigator() {
        return new InnerNavigator();
    }

    @Singleton
    public ActivitySecureManager activitySecureManager(){
        return new ActivitySecureManager();
    }

}
