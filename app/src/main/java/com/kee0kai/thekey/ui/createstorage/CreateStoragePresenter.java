package com.kee0kai.thekey.ui.createstorage;

import static com.kee0kai.thekey.App.DI;

import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.adapter.CloneableHelper;
import com.kee0kai.thekey.utils.arch.FutureHolder;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class CreateStoragePresenter extends SimplePresenter {

    public final FutureHolder<SaveStorageResult> saveStorageFuture = new FutureHolder<>();

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("cr_st");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();
    private Storage originalStorage, storage;
    private ChangeStorageMode mode;

    public void init(Storage originalStorage, ChangeStorageMode mode) {
        this.originalStorage = originalStorage;
        this.storage = CloneableHelper.tryClone(originalStorage, new Storage());
        this.mode = mode != null ? mode : ChangeStorageMode.CREATE;
    }

    public void save(String passw) {
        saveStorageFuture.set(secThread.submit(() -> {
            try {
                switch (mode) {
                    case CREATE: {
                        int r = engine.createStorage(storage);
                        return r == 0 ? SaveStorageResult.SUCCESS : SaveStorageResult.ERROR;
                    }
                    case CHANGE: {
                        int r = engine.changeStorage(originalStorage, storage);
                        return r == 0 ? SaveStorageResult.SUCCESS : SaveStorageResult.ERROR;
                    }
                    case COPY: {
                        int r = engine.copyStorage(originalStorage, storage);
                        return r == 0 ? SaveStorageResult.SUCCESS : SaveStorageResult.ERROR;
                    }
                    case CHANGE_PASSW:
                        int r = engine.changeLoggedStorage(originalStorage, passw);
                        return r == 0 ? SaveStorageResult.SUCCESS : SaveStorageResult.ERROR;
                    default:
                        return SaveStorageResult.ERROR;
                }
            } finally {
                views.refreshAllViews(10);
            }
        }));
    }


    //getters and setters
    public boolean hasChanges() {
        return !Objects.equals(storage, originalStorage);
    }

    public ChangeStorageMode getMode() {
        return mode;
    }

    public Storage getOriginalStorage() {
        return originalStorage;
    }

    public Storage getStorage() {
        return storage;
    }


    public enum SaveStorageResult {
        SUCCESS,
        EMPTY_STORAGE_PATH_ERROR,
        FILE_EXIST_ERROR,
        ERROR
    }

    public enum ChangeStorageMode {
        CREATE,// создание нового хранилища
        CHANGE,//  изменение перемещение хранилища
        DETAILS, // детали хранилища, недоступны изменения
        COPY, // коприрование хранилища
        CHANGE_PASSW // изменение текущего хранилища
    }


}
