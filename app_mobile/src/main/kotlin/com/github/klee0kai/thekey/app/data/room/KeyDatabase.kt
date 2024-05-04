package com.github.klee0kai.thekey.app.data.room

import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.github.klee0kai.thekey.app.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.app.data.room.entry.ColorGroupEntry
import com.github.klee0kai.thekey.app.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.app.data.room.entry.StorageFileEntry
import com.github.klee0kai.thekey.app.di.DI

@Database(
    entities = [
        StorageFileEntry::class,
        ColorGroupEntry::class,
        SettingPairEntry::class,
    ],
    version = 1,
)
abstract class KeyDatabase : RoomDatabase() {

    abstract fun storagesDao(): StorageFilesDao

    abstract fun colorGroupsDao(): ColorGroupDao

    abstract fun settingsDao(): SettingDao


    companion object {
        fun create(): KeyDatabase =
            databaseBuilder(DI.ctx(), KeyDatabase::class.java, "db")
                .allowMainThreadQueries()
                .build()
    }

}