package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.lifecycle.StoneLifeCycleOwner;
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


    void inject(StoragesActivity storagesActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(NoteListFragment noteListFragment, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(GenPasswFragment genPasswFragment, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(NoteActivity noteActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(LoginActivity loginActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(HistActivity histActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(FileProviderActivity fileProviderActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(EditStorageActivity editStorageActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(BaseActivity baseActivity, StoneLifeCycleOwner iStoneLifeCycleOwner);

}
