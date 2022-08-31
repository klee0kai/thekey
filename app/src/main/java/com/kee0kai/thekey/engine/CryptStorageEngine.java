package com.kee0kai.thekey.engine;

import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.engine.model.DecryptedPassw;
import com.kee0kai.thekey.model.Storage;

public class CryptStorageEngine {

    static {
        System.loadLibrary("crypt-storage-lib");
    }

    public native int createStorage(Storage storage);

    public native int changeStorage(Storage original, Storage storage);

    public native int copyStorage(Storage original, Storage storage);

    public native int isLogined();

    public native String getLoggedStoragePath();

    public native void login(String path, String passw);

    public native int changeLoggedStorage(Storage original, String passw);

    public native void unlogin();

    public native long[] getNotes();

    public native long[] getGenPasswds();

    public native DecryptedNote getNote(long note, boolean decryptPassw);

    public native long createNote();

    public native void setNote(long ptnote, DecryptedNote note);

    public native void rmNote(long note);

    public native DecryptedPassw getGenPassw(long note);

    public native String generateNewPassw(int len, int genPasswEncoding);


    public static class GenPasswEncoding {
        public static final int ENC_PASSW_NUM_ONLY = 0;
        public static final int ENC_PASSW_EN_NUM = 1;
        public static final int ENC_PASSW_EN_NUM_SPEC_SYMBOLS = 2;
    }


}
