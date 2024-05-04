package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.data.room.KeyDatabase
import com.github.klee0kai.thekey.app.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao

@Module
open class DBModule {

    @Provide(cache = Provide.CacheType.Strong)
    open fun db(): KeyDatabase = KeyDatabase.create()

    @Provide(cache = Provide.CacheType.Soft)
    open fun settingDao(db: KeyDatabase): SettingDao = db.settingsDao()

    @Provide(cache = Provide.CacheType.Soft)
    open fun storageDao(db: KeyDatabase): StorageFilesDao = db.storagesDao()

    @Provide(cache = Provide.CacheType.Soft)
    open fun coloredGroupsDao(db: KeyDatabase): ColorGroupDao = db.colorGroupsDao()

}