package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.wrappers.AsyncProvide
import com.github.klee0kai.thekey.app.data.room.KeyDatabase
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao

interface DBDependencies {

    fun dbLazy(): AsyncProvide<KeyDatabase>

    fun settingDaoLazy(): AsyncProvide<SettingDao>

    fun storageDaoLazy(): AsyncProvide<StorageFilesDao>

}