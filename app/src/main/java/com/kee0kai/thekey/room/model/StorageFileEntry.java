package com.kee0kai.thekey.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kee0kai.thekey.room.dao.StorageFilesDao;

@Entity(tableName = StorageFilesDao.TABLE_NAME)
public class StorageFileEntry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    public StorageFileEntry() {
    }

    public StorageFileEntry(String path, String name, String description) {
        this.path = path;
        this.name = name;
        this.description = description;
    }
}
