package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.ui.notes.model.NoteItem;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.adapter.SimpleDiffUtilHelper;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class NoteListPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("notes");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    private List<NoteItem> allNotes = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SimpleDiffUtilHelper<ICloneable> flatListDiffUtil = new SimpleDiffUtilHelper<>();

    public void init(boolean force) {
        refreshData();
    }

    public void refreshData() {
        if (secThread.getActiveCount() <= 0)
            secThread.submit(() -> {
                flatListDiffUtil.saveOld(flatList);
                long[] notePts = engine.getNotes();

                List<NoteItem> notesItems = new ArrayList<>(notePts.length);
                for (int i = 0; i < notePts.length; i++) {
                    DecryptedNote decryptedNote = engine.getNote(notePts[i], false);
                    notesItems.add(new NoteItem(notePts[i], decryptedNote));
                }

                allNotes = notesItems;
                flatList = flatList(notesItems);
                flatListDiffUtil.calculateWith(flatList);
                views.refreshAllViews();
            });
    }

    public void delNote(long ptNote) {
        secThread.submit(() -> {
            engine.rmNote(ptNote);
            Threads.runMain(this::refreshData);
        });
    }


    //getters and setters
    public SimpleDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }


    //private
    private List<ICloneable> flatList(List<NoteItem> notes) {
        return new ArrayList<>(notes);
    }


}
