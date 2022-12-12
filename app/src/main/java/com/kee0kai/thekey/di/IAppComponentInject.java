package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.types.lifecycle.IStoneLifeCycleOwner;
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


    void inject(StoragesActivity storagesActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(NoteListFragment noteListFragment, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(GenPasswFragment genPasswFragment, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(NoteActivity noteActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(LoginActivity loginActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(HistActivity histActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(FileProviderActivity fileProviderActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(EditStorageActivity editStorageActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

    void inject(BaseActivity baseActivity, IStoneLifeCycleOwner iStoneLifeCycleOwner);

}
