package com.kee0kai.thekey.ui.storage;

import static com.kee0kai.thekey.App.DI;

import android.text.TextUtils;

import com.github.klee0kai.hummus.adapterdelegates.diffutil.ListDiffResult;
import com.github.klee0kai.hummus.adapterdelegates.diffutil.SameDiffUtilHelper;
import com.github.klee0kai.hummus.arch.mvp.SimplePresenter;
import com.github.klee0kai.hummus.collections.ListUtils;
import com.github.klee0kai.hummus.model.ICloneable;
import com.github.klee0kai.hummus.threads.AndroidThreads;
import com.github.klee0kai.hummus.threads.FutureHolder;
import com.github.klee0kai.hummus.threads.Threads;
import com.kee0kai.thekey.domain.StorageFilesRepository;
import com.kee0kai.thekey.engine.FindStorageEngine;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.android.UserShortPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoragesPresenter extends SimplePresenter {

    private static final long RESTART_TIMEOUT = TimeUnit.MINUTES.toMillis(2);

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("storages");
    private final StorageFilesRepository rep = DI.domain().storageFilesRepository();
    private final FindStorageEngine engine = DI.engine().findStorageEngine();

    public final FutureHolder refreshDateFuture = new FutureHolder<>();

    private String searchQuery = null;
    private String deletingStoragePath = null;
    private long lastStartTime = 0;
    private int foundStorageCount = 0;


    private List<Storage> allStorages = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SameDiffUtilHelper<ICloneable> flatListDiffUtil = new SameDiffUtilHelper<>();

    public void refreshData(boolean force) {
        long curTime = System.currentTimeMillis();
        if (force) {
            deletingStoragePath = null;
            searchQuery = null;
        }
        if (!refreshDateFuture.isInProcess())
            refreshDateFuture.set(secThread.submit(() -> {
                try {
                    views.refreshAllViews();   //show loading

                    //load from repository current list
                    flatListDiffUtil.saveOld(flatList, true);
                    this.allStorages = Arrays.asList(rep.getStorages());
                    this.flatList = flatList(allStorages);
                    flatListDiffUtil.calculateWith(flatList);
                    views.refreshAllViews();   //show loading and current list


                    int found = findStoragesOnDevice(force);
                    if (found > 0) {
                        //update new list
                        flatListDiffUtil.saveOld(flatList, true);
                        this.allStorages = Arrays.asList(rep.getStorages());
                        this.flatList = flatList(allStorages);
                        flatListDiffUtil.calculateWith(flatList);
                    }
                } finally {
                    views.refreshAllViews(10);
                }
            }));
    }

    public void delStorage(boolean accept) {
        secThread.submit(() -> {
            if (deletingStoragePath == null)
                return;
            if (!accept) {
                deletingStoragePath = null;
                return;
            }
            new File(deletingStoragePath).deleteOnExit();
            rep.deleteStorage(deletingStoragePath);
            AndroidThreads.runMain(() -> refreshData(false));
        });

    }


    public void search(String query) {
        String finalQuery = query.toLowerCase(Locale.ROOT);
        if (Objects.equals(finalQuery, this.searchQuery))
            return;
        this.searchQuery = finalQuery;
        secThread.submit(() -> {
            if (!Objects.equals(finalQuery, this.searchQuery))
                //search query changed
                return;
            flatListDiffUtil.saveOld(flatList, true);
            flatList = flatList(allStorages);
            flatListDiffUtil.calculateWith(flatList);
            views.refreshAllViews();
        });
    }

    //getters and setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setDeletingStoragePath(String deletingStoragePath) {
        this.deletingStoragePath = deletingStoragePath;
    }

    public String getDeletingStoragePath() {
        return deletingStoragePath;
    }

    public List<ICloneable> getFlatList() {
        return flatList;
    }

    public ListDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }

    //  private
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

    private List<ICloneable> flatList(List<Storage> storages) {
        List<Storage> filtered = ListUtils.filter(storages, (i, it) -> TextUtils.isEmpty(searchQuery) ||
                it.path != null && it.path.toLowerCase(Locale.ROOT).contains(searchQuery) ||
                it.name != null && it.name.toLowerCase(Locale.ROOT).contains(searchQuery));
        Collections.sort(filtered);
        return new ArrayList<>(filtered);
    }

}
