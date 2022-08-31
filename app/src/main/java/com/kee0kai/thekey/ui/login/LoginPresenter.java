package com.kee0kai.thekey.ui.login;

import static com.kee0kai.thekey.App.DI;

import android.net.Uri;
import android.text.TextUtils;

import com.kee0kai.thekey.domain.AppSettingsRepository;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.arch.FutureHolder;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class LoginPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("login");
    private final AppSettingsRepository apSetRep = DI.domain().appSettingsRepository();
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    private final FutureHolder<Boolean> loginFuture = new FutureHolder<>();

    private String storagePath;
    private Storage storageInfo;


    public void init() {

    }

    public void login(String passw) {
        if (!TextUtils.isEmpty(storagePath) && !loginFuture.isInProcess())
            loginFuture.set(secThread.submit(() -> {
                try {
                    engine.login(storagePath, passw);
                    return engine.isLogined() != 0 ? Boolean.TRUE : Boolean.FALSE;
                } finally {
                    views.refreshAllViews(10);
                }
            }));
    }

    public void unlogin() {
        loginFuture.cancel();
        secThread.submit(() -> {
            try {
                engine.unlogin();
            } finally {
                views.refreshAllViews(10);
            }
        });
    }

    public void setStorage(Uri uri) {

    }


    //getters and setters
    public String getStoragePath() {
        return storagePath;
    }

    public Storage getStorageInfo() {
        return storageInfo;
    }
}
