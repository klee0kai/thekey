package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.ui.hist.HistPresenter;
import com.kee0kai.thekey.ui.login.LoginPresenter;
import com.kee0kai.thekey.ui.note.NotePresenter;
import com.kee0kai.thekey.ui.notes.gen.GenPasswPresenter;
import com.kee0kai.thekey.ui.notes.notelist.NoteListPresenter;
import com.kee0kai.thekey.ui.storage.StoragesPresenter;

@Module
public class PresenterModule {

    @Singleton(cache = Singleton.CacheType.WEAK)
    public LoginPresenter loginPresenter() {
        return new LoginPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public StoragesPresenter storagesPresenter() {
        return new StoragesPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public EditStoragePresenter editStoragePresenter() {
        return new EditStoragePresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public NoteListPresenter noteListPresenter() {
        return new NoteListPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public GenPasswPresenter genPasswPresenter() {
        return new GenPasswPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public HistPresenter histPresenter() {
        return new HistPresenter();
    }

    @Singleton(cache = Singleton.CacheType.WEAK)
    public NotePresenter notePresenter(){
        return new NotePresenter();
    }

}
