package com.github.klee0kai.thekey.app.data.room

import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.app.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.app.data.room.entry.StorageFileEntry
import com.github.klee0kai.thekey.app.di.DI

@Database(
    entities = [
        StorageFileEntry::class,
        SettingPairEntry::class],
    version = 1,
)
abstract class KeyDatabase : RoomDatabase() {

    abstract fun storagesDao(): StorageFilesDao

    abstract fun settingsDao(): SettingDao

    companion object {
        fun create(): KeyDatabase =
            databaseBuilder(DI.app(), KeyDatabase::class.java, "db")
                .allowMainThreadQueries()
                .build()
    }

}