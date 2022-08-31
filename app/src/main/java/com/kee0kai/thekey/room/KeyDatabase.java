package com.kee0kai.thekey.room;

import static com.kee0kai.thekey.App.DI;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kee0kai.thekey.room.dao.SettingDao;
import com.kee0kai.thekey.room.dao.StorageFilesDao;
import com.kee0kai.thekey.room.model.SettingPairEntry;
import com.kee0kai.thekey.room.model.StorageFileEntry;

@Database(entities =
        {
                StorageFileEntry.class,
                SettingPairEntry.class
        }, version = 1)
public abstract class KeyDatabase extends RoomDatabase {

    public abstract StorageFilesDao cachedFilesDao();

    public abstract SettingDao settingsDao();

    public static KeyDatabase create() {
        return Room.databaseBuilder(DI.app().application(), KeyDatabase.class, "db")
                .allowMainThreadQueries()
                .build();
    }


}
