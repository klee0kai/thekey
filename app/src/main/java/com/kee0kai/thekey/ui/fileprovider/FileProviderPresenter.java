package com.kee0kai.thekey.ui.fileprovider;

import android.os.Environment;
import android.text.TextUtils;

import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.adapter.SimpleDiffUtilHelper;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;
import com.kee0kai.thekey.utils.collections.ListsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class FileProviderPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("prov");
    private File curPath = null;
    private WorkMode mode = WorkMode.OPEN_STORAGE;

    private String searchQuery = null;
    private List<FileItem> files = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SimpleDiffUtilHelper<ICloneable> flatListDiffUtil = new SimpleDiffUtilHelper();


    public void init(WorkMode mode, boolean force) {
        if (force || curPath == null)
            curPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        this.mode = mode;
        refreshData();
    }

    public void refreshData() {
        secThread.submit(() -> {
            if (curPath == null)
                return;

            flatListDiffUtil.saveOld(flatList);
            File[] files = curPath.listFiles();

            ArrayList<FileItem> fileItems = new ArrayList<>(files != null ? files.length : 0);
            for (int i = 0; files != null && i < files.length; i++) {
                fileItems.add(new FileItem(files[i].getName(), files[i].isFile()));
            }

            this.files = fileItems;
            this.flatList = flatList(this.files);
            flatListDiffUtil.calculateWith(flatList);
            views.refreshAllViews(5);
        });
    }

    public void openDir(String name) {
        curPath = new File(curPath.getAbsolutePath(), name);
        refreshData();
    }

    public boolean toUp() {
        if (Objects.equals(curPath.getAbsolutePath(), "/")
                || curPath.getParentFile() == null
                || curPath.getParentFile().list() == null)
            return false;
        curPath = curPath.getParentFile();
        refreshData();
        return true;
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
            flatListDiffUtil.saveOld(flatList);
            flatList = flatList(files);
            flatListDiffUtil.calculateWith(flatList);
            views.refreshAllViews();
        });
    }

    //getters and setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public SimpleDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }

    public File getCurPath() {
        return curPath;
    }

    public WorkMode getMode() {
        return mode;
    }

    private List<ICloneable> flatList(List<FileItem> files) {
        List<FileItem> filtered = ListsUtils.filter(files, (i, it) -> TextUtils.isEmpty(searchQuery) ||
                it.name != null && it.name.toLowerCase(Locale.ROOT).contains(searchQuery));
        Collections.sort(filtered);
        return new ArrayList<>(filtered);
    }


    public enum WorkMode {
        OPEN_STORAGE,
        CREATE_STORAGE
    }
}
