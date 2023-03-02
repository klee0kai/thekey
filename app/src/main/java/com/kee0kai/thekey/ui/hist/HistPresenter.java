package com.kee0kai.thekey.ui.hist;

import static com.kee0kai.thekey.App.DI;

import com.github.klee0kai.hummus.adapterdelegates.diffutil.ListDiffResult;
import com.github.klee0kai.hummus.adapterdelegates.diffutil.SameDiffUtilHelper;
import com.github.klee0kai.hummus.model.ICloneable;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.engine.model.DecryptedPassw;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class HistPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("hist");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    private List<DecryptedPassw> allPassw = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SameDiffUtilHelper<ICloneable> flatListDiffUtil = new SameDiffUtilHelper();

    private long ptNote = 0;

    public void init(long pNote, boolean force) {
        if (!force && this.ptNote == pNote)
            return;
        this.ptNote = pNote;
        this.allPassw = Collections.emptyList();
        this.flatList = Collections.emptyList();
        refreshData();
    }

    public void refreshData() {
        secThread.submit(() -> {
            flatListDiffUtil.saveOld(flatList, true);
            if (ptNote != 0) {
                DecryptedNote note = engine.getNote(ptNote, true);
                allPassw = note != null && note.hist != null ? Arrays.asList(note.hist) : Collections.emptyList();
            } else {
                long[] passwdsIds = engine.getGenPasswds();
                List<DecryptedPassw> genPassw = new ArrayList<>(passwdsIds.length);
                for (int i = 0; i < passwdsIds.length; i++) {
                    genPassw.add(engine.getGenPassw(passwdsIds[passwdsIds.length - 1 - i]));
                }
                allPassw = genPassw;
            }
            flatList = flatList(allPassw);
            flatListDiffUtil.calculateWith(flatList);
            views.refreshAllViews();
        });
    }

    //getters and setters

    public ListDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }


    //private
    private List<ICloneable> flatList(List<DecryptedPassw> passws) {
        return new ArrayList<>(passws);
    }


}
