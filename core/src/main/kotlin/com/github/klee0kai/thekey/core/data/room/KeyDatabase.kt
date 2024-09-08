package com.github.klee0kai.thekey.core.data.room

import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.github.klee0kai.thekey.core.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.NoteColorGroupDao
import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.core.data.room.entry.ColorGroupEntry
import com.github.klee0kai.thekey.core.data.room.entry.NoteColorGroupEntry
import com.github.klee0kai.thekey.core.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.core.data.room.entry.StorageFileEntry
import com.github.klee0kai.thekey.core.di.CoreDI

@Database(
    entities = [
        StorageFileEntry::class,
        ColorGroupEntry::class,
        NoteColorGroupEntry::class,
        SettingPairEntry::class,
    ],
    version = 1,
)
abstract class KeyDatabase : RoomDatabase() {

    abstract fun storagesDao(): StorageFilesDao

    abstract fun colorGroupsDao(): ColorGroupDao

    abstract fun noteColorGroupsDao(): NoteColorGroupDao

    abstract fun settingsDao(): SettingDao


    companion object {
        fun create(): KeyDatabase =
            databaseBuilder(CoreDI.ctx(), KeyDatabase::class.java, "db")
                .allowMainThreadQueries()
                .build()
    }

}