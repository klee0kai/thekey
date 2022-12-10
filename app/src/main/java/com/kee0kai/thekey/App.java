package com.kee0kai.thekey;

import android.app.Application;

import com.github.klee0kai.stone.Stone;
import com.kee0kai.thekey.di.AppComponent;
import com.kee0kai.thekey.di.ControlModule;
import com.kee0kai.thekey.di.DomainModule;
import com.kee0kai.thekey.di.EngineModule;
import com.kee0kai.thekey.di.PresenterModule;
import com.kee0kai.thekey.di.ProviderModule;

public class App extends Application {

    public static final String STORAGE_EXT = "ckey";

    public static final AppComponent DI = Stone.createComponent(AppComponent.class);

    @Override
    public void onCreate() {
        super.onCreate();
        DI.bind(this);
    }

}
