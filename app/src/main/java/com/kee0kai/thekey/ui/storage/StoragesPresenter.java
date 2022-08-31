package com.kee0kai.thekey.ui.storage;

import static com.kee0kai.thekey.App.DI;

import androidx.recyclerview.widget.DiffUtil;

import com.kee0kai.thekey.domain.StorageFilesRepository;
import com.kee0kai.thekey.engine.FindStorageEngine;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.adapter.SimpleDiffUtilHelper;
import com.kee0kai.thekey.utils.android.UserShortPaths;
import com.kee0kai.thekey.utils.arch.FutureHolder;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoragesPresenter extends SimplePresenter {

    private static final long RESTART_TIMEOUT = TimeUnit.MINUTES.toMillis(2);

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("storages");
    private final StorageFilesRepository rep = DI.domain().storageFilesRepository();
    private final FindStorageEngine engine = DI.engine().findStorageEngine();

    public final FutureHolder refreshDateFuture = new FutureHolder<>();

    private long lastStartTime = 0;
    private int foundStorageCount = 0;


    private List<Storage> allStorages = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SimpleDiffUtilHelper<ICloneable> flatListDiffUtil = new SimpleDiffUtilHelper();

    public void refreshData(boolean force) {
        long curTime = System.currentTimeMillis();
        if (!refreshDateFuture.isInProcess())
            refreshDateFuture.set(secThread.submit(() -> {
                try {
                    findStoragesOnDevice(force);
                    flatListDiffUtil.saveOld(flatList);

                    this.allStorages = Arrays.asList(rep.getStorages());
                    this.flatList = flatList(allStorages);
                    flatListDiffUtil.calculateWith(flatList);
                } finally {
                    views.refreshAllViews(10);
                }
            }));
    }


    public void delStorage(Storage storage) {
        secThread.submit(() -> {
            new File(storage.path).delete();
            rep.deleteStorage(storage.path);
            Threads.runMain(() -> refreshData(false));
        });

    }

    private int findStoragesOnDevice(boolean force) {
        long curTime = System.currentTimeMillis();
        if (!force && curTime - lastStartTime < RESTART_TIMEOUT)
            return 0;
        lastStartTime = curTime;
        foundStorageCount = 0;
        engine.setFindListener(storage -> {
            foundStorageCount++;
            rep.addStorage(storage);
        });
        for (String sDir : UserShortPaths.getRootPaths(false)) {
            engine.findStorage(sDir);
        }
        return foundStorageCount;
    }

    //getters and setters
    public SimpleDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }


    private List<ICloneable> flatList(List<Storage> storages) {
        return new ArrayList<>(storages);
    }

}
