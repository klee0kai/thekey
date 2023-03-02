package com.kee0kai.thekey.ui.login;

import static com.kee0kai.thekey.App.DI;

import android.net.Uri;
import android.text.TextUtils;

import com.github.klee0kai.hummus.arch.mvp.SimplePresenter;
import com.github.klee0kai.hummus.threads.FutureHolder;
import com.github.klee0kai.hummus.threads.Threads;
import com.kee0kai.thekey.domain.AppSettingsRepository;
import com.kee0kai.thekey.domain.StorageFilesRepository;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.model.Storage;

import java.util.concurrent.ThreadPoolExecutor;

public class LoginPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("login");
    private final AppSettingsRepository apSetRep = DI.domain().appSettingsRepository();
    private final StorageFilesRepository rep = DI.domain().storageFilesRepository();
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    public final FutureHolder<Boolean> loginFuture = new FutureHolder<>();

    private String storagePath;
    private Storage storageInfo;


    public void init() {
        storagePath = apSetRep.get(AppSettingsRepository.SETTING_DEFAULT_STORAGE_PATH,
                DI.app().application().getApplicationInfo().dataDir + "/keys.ckey");
        storageInfo = rep.findStorage(storagePath);
        views.refreshAllViews();

    }

    public void login(String passw) {
        if (!TextUtils.isEmpty(storagePath) && !loginFuture.isInProcess())
            loginFuture.set(secThread.submit(() -> {
                try {

                    views.refreshAllViews();   //show loading

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
        if (uri == null)
            return;
        apSetRep.set(AppSettingsRepository.SETTING_DEFAULT_STORAGE_PATH, uri.getPath());
        storagePath = uri.getPath();
        storageInfo = rep.findStorage(storagePath);
        views.refreshAllViews();
    }


    //getters and setters
    public String getStoragePath() {
        return storagePath;
    }

    public Storage getStorageInfo() {
        return storageInfo;
    }
}
