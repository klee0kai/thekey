package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import android.text.TextUtils;

import com.github.klee0kai.hummus.adapterdelegates.diffutil.ListDiffResult;
import com.github.klee0kai.hummus.adapterdelegates.diffutil.SameDiffUtilHelper;
import com.github.klee0kai.hummus.arch.mvp.SimplePresenter;
import com.github.klee0kai.hummus.collections.ListUtils;
import com.github.klee0kai.hummus.model.ICloneable;
import com.github.klee0kai.hummus.threads.AndroidThreads;
import com.github.klee0kai.hummus.threads.Threads;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.ui.notes.model.NoteItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class NoteListPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("notes");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();

    private String searchQuery = null;
    private long deletingPtNote = 0;
    private List<NoteItem> allNotes = Collections.emptyList();
    private List<ICloneable> flatList = Collections.emptyList();
    private final SameDiffUtilHelper<ICloneable> flatListDiffUtil = new SameDiffUtilHelper<>();

    public void init(boolean force) {
        if (force) {
            searchQuery = null;
            deletingPtNote = 0;
        }
        refreshData();
    }

    public void refreshData() {
        if (secThread.getActiveCount() <= 0)
            secThread.submit(() -> {
                flatListDiffUtil.saveOld(flatList, true);
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

    public void delNote(boolean accept) {
        secThread.submit(() -> {
            if (deletingPtNote == 0)
                return;
            if (!accept) {
                deletingPtNote = 0;
                return;
            }
            engine.rmNote(deletingPtNote);
            AndroidThreads.runMain(this::refreshData);
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
            flatList = flatList(allNotes);
            flatListDiffUtil.calculateWith(flatList);
            views.refreshAllViews();
        });
    }


    //getters and setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public ListDiffResult<ICloneable> popFlatListChanges() {
        return flatListDiffUtil.popDiffResult(flatList);
    }

    public void setDeletingPtNote(long deletingPtNote) {
        this.deletingPtNote = deletingPtNote;
    }

    public long getDeletingPtNote() {
        return deletingPtNote;
    }

    //private
    private List<ICloneable> flatList(List<NoteItem> notes) {
        List<NoteItem> filtered = ListUtils.filter(notes, (i, it) -> TextUtils.isEmpty(searchQuery) ||
                it.decryptedNote.site != null && it.decryptedNote.site.toLowerCase(Locale.ROOT).contains(searchQuery) ||
                it.decryptedNote.login != null && it.decryptedNote.login.toLowerCase(Locale.ROOT).contains(searchQuery));
        Collections.sort(filtered, (o1, o2) -> o1.decryptedNote.compareTo(o2.decryptedNote));
        return new ArrayList<>(filtered);
    }


}
