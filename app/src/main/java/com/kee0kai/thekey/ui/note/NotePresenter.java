package com.kee0kai.thekey.ui.note;

import static com.kee0kai.thekey.App.DI;

import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.utils.adapter.CloneableHelper;
import com.kee0kai.thekey.utils.arch.FutureHolder;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class NotePresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("note");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();
    private long ptNote;
    private DecryptedNote originNote = null, note = null;

    public final FutureHolder<Boolean> saveFuture = new FutureHolder<>();

    public void init(long ptNote, boolean force) {
        if (!force && this.ptNote == ptNote)
            return;
        this.ptNote = ptNote;
        secThread.submit(() -> {
            originNote = engine.getNote(ptNote, true);
            if (originNote == null) originNote = new DecryptedNote();
            note = CloneableHelper.tryClone(originNote, new DecryptedNote());
            views.refreshAllViews();
        });
    }

    public void save() {
        if (!saveFuture.isInProcess())
            saveFuture.set(secThread.submit(() -> {
                if (ptNote <= 0)
                    ptNote = engine.createNote();
                if (originNote != null) {
                    //todo сделать в движке частичное изменение записи
                    note.hist = originNote.hist;
                }
                engine.setNote(ptNote, note);
                views.refreshAllViews();
                return Boolean.TRUE;
            }));
    }

    public void genPassw() {
        secThread.submit(() -> {
            int genPasswLen = originNote != null && originNote.passw != null && originNote.passw.length() > 3 ? originNote.passw.length() : 6;
            note.passw = engine.generateNewPassw(genPasswLen, CryptStorageEngine.GenPasswEncoding.ENC_PASSW_EN_NUM);
            views.refreshAllViews(10);
        });
    }


    //getters and setters
    public void setNote(DecryptedNote note) {
        this.note = note;
        views.refreshAllViews();
    }

    public DecryptedNote getNote() {
        return note;
    }

    public DecryptedNote getOriginNote() {
        return originNote;
    }

    public long getPtNote() {
        return ptNote;
    }

    public boolean isSaveAvailable() {
        return originNote == null || note != null &&
                !(Objects.equals(originNote.site, note.site) &&
                        Objects.equals(originNote.login, note.login) &&
                        Objects.equals(originNote.passw, note.passw) &&
                        Objects.equals(originNote.desc, note.desc));
    }
}
