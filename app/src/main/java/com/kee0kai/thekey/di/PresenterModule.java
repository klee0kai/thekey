package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.ui.createstorage.CreateStoragePresenter;
import com.kee0kai.thekey.ui.login.LoginPresenter;
import com.kee0kai.thekey.ui.storage.StoragesPresenter;

@Module
public class PresenterModule {

    @Singleton(cache = Singleton.CacheType.WEAK)
    public LoginPresenter loginPresenter() {
        return new LoginPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public StoragesPresenter storagesPresenter() {
        return new StoragesPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public CreateStoragePresenter createStoragePresenter() {
        return new CreateStoragePresenter();
    }

}
