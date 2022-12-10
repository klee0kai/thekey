package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.Module;
import com.github.klee0kai.stone.annotations.module.Provide;
import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.ui.fileprovider.FileProviderPresenter;
import com.kee0kai.thekey.ui.hist.HistPresenter;
import com.kee0kai.thekey.ui.login.LoginPresenter;
import com.kee0kai.thekey.ui.note.NotePresenter;
import com.kee0kai.thekey.ui.notes.gen.GenPasswPresenter;
import com.kee0kai.thekey.ui.notes.notelist.NoteListPresenter;
import com.kee0kai.thekey.ui.storage.StoragesPresenter;

@Module
public interface PresenterModule {

    @Provide(cache = Provide.CacheType.Weak)
    public LoginPresenter loginPresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public StoragesPresenter storagesPresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public EditStoragePresenter editStoragePresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public NoteListPresenter noteListPresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public GenPasswPresenter genPasswPresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public HistPresenter histPresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public NotePresenter notePresenter();

    @Provide(cache = Provide.CacheType.Weak)
    public FileProviderPresenter fileProviderPresenter();

}
