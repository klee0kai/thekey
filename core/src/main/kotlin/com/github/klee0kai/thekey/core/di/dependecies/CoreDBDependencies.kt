package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.data.room.KeyDatabase
import com.github.klee0kai.thekey.core.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.NoteColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface CoreDBDependencies {

    fun dbLazy(): AsyncCoroutineProvide<KeyDatabase>

    fun settingDaoLazy(): AsyncCoroutineProvide<SettingDao>

    fun storageDaoLazy(): AsyncCoroutineProvide<StorageFilesDao>

    fun colorGroupDaoLazy(): AsyncCoroutineProvide<ColorGroupDao>

    fun noteColorGroupDaoLazy(): AsyncCoroutineProvide<NoteColorGroupDao>

}