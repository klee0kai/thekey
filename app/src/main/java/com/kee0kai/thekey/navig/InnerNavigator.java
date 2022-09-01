package com.kee0kai.thekey.navig;

import static com.kee0kai.thekey.App.DI;

import android.content.Intent;

import com.kee0kai.thekey.ui.hist.HistActivity;
import com.kee0kai.thekey.ui.note.NoteActivity;
import com.kee0kai.thekey.ui.notes.NotesListActivity;

public class InnerNavigator {


    public Intent notes() {
        return new Intent(DI.app().application(), NotesListActivity.class);
    }


    public Intent note(long ptNote) {
        Intent intent = new Intent(DI.app().application(), NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_PTR_EXTRA, ptNote);
        return intent;
    }

    public Intent noteHist(long note) {
        Intent intent = new Intent(DI.app().application(), HistActivity.class);
        intent.putExtra(NoteActivity.NOTE_PTR_EXTRA, note);
        return intent;
    }

    public Intent getGenHist() {
        return new Intent(DI.app().application(), HistActivity.class);
    }


}
