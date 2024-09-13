package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.data.room.KeyDatabase
import com.github.klee0kai.thekey.core.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.NoteColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.data.room.dao.StorageFilesDao

@Module
interface CoreDBModule {

    @Provide(cache = Provide.CacheType.Strong)
    fun db(): KeyDatabase = KeyDatabase.create()

    @Provide(cache = Provide.CacheType.Soft)
    fun settingDao(db: KeyDatabase): SettingDao = db.settingsDao()

    @Provide(cache = Provide.CacheType.Soft)
    fun storageDao(db: KeyDatabase): StorageFilesDao = db.storagesDao()

    @Provide(cache = Provide.CacheType.Soft)
    fun coloredGroupsDao(db: KeyDatabase): ColorGroupDao = db.colorGroupsDao()

    @Provide(cache = Provide.CacheType.Soft)
    fun noteColoredGroupsDao(db: KeyDatabase): NoteColorGroupDao = db.noteColorGroupsDao()

}