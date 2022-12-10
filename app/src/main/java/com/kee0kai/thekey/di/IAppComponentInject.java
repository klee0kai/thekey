package com.kee0kai.thekey.di;

import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.ui.editstorage.EditStorageActivity;
import com.kee0kai.thekey.ui.fileprovider.FileProviderActivity;
import com.kee0kai.thekey.ui.hist.HistActivity;
import com.kee0kai.thekey.ui.login.LoginActivity;
import com.kee0kai.thekey.ui.note.NoteActivity;
import com.kee0kai.thekey.ui.notes.gen.GenPasswFragment;
import com.kee0kai.thekey.ui.notes.notelist.NoteListFragment;
import com.kee0kai.thekey.ui.storage.StoragesActivity;

public interface IAppComponentInject {


    void inject(StoragesActivity storagesActivity);

    void inject(NoteListFragment noteListFragment);

    void inject(GenPasswFragment genPasswFragment);

    void inject(NoteActivity noteActivity);

    void inject(LoginActivity loginActivity);

    void inject(HistActivity histActivity);

    void inject(FileProviderActivity fileProviderActivity);

    void inject(EditStorageActivity editStorageActivity);

    void inject(BaseActivity baseActivity);

}
