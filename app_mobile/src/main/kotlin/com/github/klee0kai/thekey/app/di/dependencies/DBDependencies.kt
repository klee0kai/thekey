package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.data.room.KeyDatabase
import com.github.klee0kai.thekey.app.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface DBDependencies {

    fun dbLazy(): AsyncCoroutineProvide<KeyDatabase>

    fun settingDaoLazy(): AsyncCoroutineProvide<SettingDao>

    fun storageDaoLazy(): AsyncCoroutineProvide<StorageFilesDao>

    fun colorGroupDaoLazy(): AsyncCoroutineProvide<ColorGroupDao>

}