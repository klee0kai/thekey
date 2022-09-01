package com.kee0kai.thekey.ui.fileprovider;

import android.os.Environment;

import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.adapter.SimpleDiffUtilHelper;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class FileProviderPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("prov");
    private File curPath = null;
    private WorkMode mode = WorkMode.OPEN_STORAGE;

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

    //getters and setters
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
        return new ArrayList<>(files);
    }


    public enum WorkMode {
        OPEN_STORAGE,
        CREATE_STORAGE
    }
}
