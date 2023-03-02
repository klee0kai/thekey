package com.kee0kai.thekey.ui.editstorage;

import static com.kee0kai.thekey.App.DI;

import com.github.klee0kai.hummus.arch.mvp.SimplePresenter;
import com.github.klee0kai.hummus.model.CloneableHelper;
import com.github.klee0kai.hummus.threads.FutureHolder;
import com.github.klee0kai.hummus.threads.Threads;
import com.kee0kai.thekey.domain.StorageFilesRepository;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.model.Storage;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class EditStoragePresenter extends SimplePresenter {

    public final FutureHolder<SaveStorageResult> saveStorageFuture = new FutureHolder<>();

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("cr_st");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();
    private final StorageFilesRepository rep = DI.domain().storageFilesRepository();
    private Storage originalStorage, storage;
    private ChangeStorageMode mode;

    public void init(String originStoragePath, ChangeStorageMode mode) {
        this.originalStorage = rep.findStorage(originStoragePath);
        this.storage = CloneableHelper.tryClone(originalStorage, new Storage());
        this.mode = mode != null ? mode : ChangeStorageMode.CREATE;
        views.refreshAllViews();
    }

    public void save(String passw) {
        saveStorageFuture.set(secThread.submit(() -> {
            try {
                switch (mode) {
                    case CREATE: {
                        int r = engine.createStorage(storage);
                        if (r != 0)
                            return SaveStorageResult.ERROR;
                        rep.addStorage(storage);
                        return SaveStorageResult.SUCCESS;
                    }
                    case EDIT_LOGGED_STORAGE:
                    case EDIT: {
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

    public void setStorage(Storage storage) {
        this.storage = storage;
        views.refreshAllViews();
    }

    public enum SaveStorageResult {
        SUCCESS,
        EMPTY_STORAGE_PATH_ERROR,
        FILE_EXIST_ERROR,
        ERROR
    }

    public enum ChangeStorageMode {
        CREATE,// создание нового хранилища
        EDIT,//  изменение перемещение хранилища
        EDIT_LOGGED_STORAGE,//  изменение хранилища, в которое уже вошли, позволяет менять пароль
        DETAILS, // детали хранилища, недоступны изменения
        COPY, // коприрование хранилища
        CHANGE_PASSW // изменение текущего хранилища
    }


}
