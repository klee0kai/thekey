package com.kee0kai.thekey.navig;

import static com.kee0kai.thekey.App.DI;

import android.content.Intent;

import com.kee0kai.thekey.ui.hist.HistActivity;
import com.kee0kai.thekey.ui.notes.NotesListActivity;

public class InnerNavigator {


    public Intent notes() {
        return new Intent(DI.app().application(), NotesListActivity.class);
    }


    public Intent noteHist(long note) {
        Intent intent = new Intent(DI.app().application(), HistActivity.class);
        intent.putExtra(HistActivity.NOTE_ID_EXTRA, note);
        return intent;
    }

    public Intent getGenHist() {
        return new Intent(DI.app().application(), HistActivity.class);
    }


}
