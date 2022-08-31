package com.kee0kai.thekey;

import android.app.Application;

import com.github.klee0kai.stone.Stone;
import com.kee0kai.thekey.di.AppComponent;

public class App extends Application {

    public static final AppComponent DI = Stone.createComponent(AppComponent.class);

    @Override
    public void onCreate() {
        super.onCreate();
        DI.init(this);

    }
}
