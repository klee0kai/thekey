package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import android.text.TextUtils;

import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.ui.notes.model.NoteItem;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.adapter.SimpleDiffUtilHelper;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;
import com.kee0kai.thekey.utils.collections.ListsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class NoteListPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("notes");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    private String searchQuery = null;
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
            flatList = flatList(allNotes);
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


    //private
    private List<ICloneable> flatList(List<NoteItem> notes) {
        List<NoteItem> filtered = ListsUtils.filter(notes, (i, it) -> TextUtils.isEmpty(searchQuery) ||
                it.decryptedNote.site != null && it.decryptedNote.site.toLowerCase(Locale.ROOT).contains(searchQuery) ||
                it.decryptedNote.login != null && it.decryptedNote.login.toLowerCase(Locale.ROOT).contains(searchQuery));
        Collections.sort(filtered, (o1, o2) -> o1.decryptedNote.compareTo(o2.decryptedNote));
        return new ArrayList<>(filtered);
    }


}
