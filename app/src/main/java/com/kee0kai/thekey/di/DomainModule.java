package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Module;
import com.github.klee0kai.stone.annotations.Singleton;
import com.kee0kai.thekey.domain.AppSettingsRepository;
import com.kee0kai.thekey.domain.StorageFilesRepository;

@Module
public class DomainModule {

    @Singleton
    public AppSettingsRepository appSettingsRepository() {
        return new AppSettingsRepository();
    }

    @Singleton
    public StorageFilesRepository storageFilesRepository() {
        return new StorageFilesRepository();
    }

}
