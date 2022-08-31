package com.kee0kai.thekey.ui.storage;

import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.concurrent.ThreadPoolExecutor;

public class StoragesPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("storages");

    public void refreshData() {

    }



}
