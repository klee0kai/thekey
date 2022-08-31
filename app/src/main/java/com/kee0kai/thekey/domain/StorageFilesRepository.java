package com.kee0kai.thekey.domain;

import static com.kee0kai.thekey.App.DI;

import com.kee0kai.thekey.domain.room.KeyDatabase;
import com.kee0kai.thekey.domain.room.model.StorageFileEntry;
import com.kee0kai.thekey.model.Storage;

import java.io.File;

public class StorageFilesRepository {

    private final KeyDatabase db = DI.provider().keyDatabase();


    public Storage[] getStorages() {
        StorageFileEntry[] fileEntries = db.cachedFilesDao().get();
        Storage[] storages = new Storage[fileEntries.length];
        for (int i = 0; i < fileEntries.length; i++) {
            storages[i] = new Storage(fileEntries[i].path, fileEntries[i].name, fileEntries[i].description);
        }
        return storages;
    }

    public void addStorage(Storage storage){
        StorageFileEntry cachedStorage = db.cachedFilesDao().get(storage.path);

        StorageFileEntry fileEntry = new StorageFileEntry(storage.path, storage.name, storage.description);
        if (cachedStorage != null) fileEntry.id = cachedStorage.id;
        db.cachedFilesDao().insert(fileEntry);
    }

    public Storage findStorage(String path) {
        StorageFileEntry storageFileEntry = db.cachedFilesDao().get(path);
        return storageFileEntry != null ? new Storage(storageFileEntry.path, storageFileEntry.name, storageFileEntry.description) : null;
    }

    public void deleteStorage(String path) {
        new File(path).delete();
        db.cachedFilesDao().delete(path);
    }

}
