package com.kee0kai.thekey.ui.createstorage;

import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.concurrent.ThreadPoolExecutor;

public class CreateStoragePresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("cr_st");


    public void init() {

    }

}
