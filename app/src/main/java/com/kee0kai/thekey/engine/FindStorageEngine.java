package com.kee0kai.thekey.engine;

import com.kee0kai.thekey.model.Storage;

public class FindStorageEngine {

    static {
        System.loadLibrary("crypt-storage-lib");
    }

    private static FileFindListener findListener = null;


    public native void findStorage(String sourceDir);


    public void setFindListener(FileFindListener findListener) {
        FindStorageEngine.findListener = findListener;
    }

    // from Jni
    private void onStorageFounded(Storage storage) {
        if (findListener != null)
            findListener.onStorageFound(storage);
    }

    public interface FileFindListener {
        void onStorageFound(Storage storage);
    }

}
