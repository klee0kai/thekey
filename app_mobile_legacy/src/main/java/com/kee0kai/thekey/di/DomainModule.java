package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.module.Module;
import com.github.klee0kai.stone.annotations.module.Provide;
import com.kee0kai.thekey.domain.AppSettingsRepository;
import com.kee0kai.thekey.domain.StorageFilesRepository;

@Module
public interface DomainModule {

    @Provide
    public AppSettingsRepository appSettingsRepository();

    @Provide
    public StorageFilesRepository storageFilesRepository();

}
