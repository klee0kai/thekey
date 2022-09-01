package com.kee0kai.thekey.navig;

import static com.kee0kai.thekey.App.DI;

import android.content.Context;
import android.content.Intent;

import com.kee0kai.thekey.ui.notes.NotesListActivity;

public class InnerNavigator {


    public Intent notes() {
        return new Intent(DI.app().application(), NotesListActivity.class);
    }


}
