package com.kee0kai.thekey.engine;

import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.engine.model.DecryptedPassw;
import com.kee0kai.thekey.model.Storage;

public class CryptStorageEngine {

    static {
        System.loadLibrary("crypt-storage-lib");
    }

    public static native int createStorage(Storage storage);

    public static native int changeStorage(Storage original, Storage storage);

    public static native int copyStorage(Storage original, Storage storage);

    public static native int isLogined();

    public static native String getLoggedStoragePath();

    public static native void login(String path, String passw);

    public static native int changeLoggedStorage(Storage original, String passw);

    public static native void unlogin();

    public static native long[] getNotes();

    public static native long[] getGenPasswds();

    public static native DecryptedNote getNote(long note, boolean decryptPassw);

    public static native long createNote();

    public static native void setNote(long ptnote, DecryptedNote note);

    public static native void rmNote(long note);

    public static native DecryptedPassw getGenPassw(long note);

    public static native String generateNewPassw(int len, int genPasswEncoding);


    public static class GenPasswEncoding {
        public static final int ENC_PASSW_NUM_ONLY = 0;
        public static final int ENC_PASSW_EN_NUM = 1;
        public static final int ENC_PASSW_EN_NUM_SPEC_SYMBOLS = 2;
    }


}
