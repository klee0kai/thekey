package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.ui.login.LoginPresenter;

@Module
public class PresenterModule {

    @Singleton(cache = Singleton.CacheType.WEAK)
    public LoginPresenter loginPresenter() {
        return new LoginPresenter();
    }

}
