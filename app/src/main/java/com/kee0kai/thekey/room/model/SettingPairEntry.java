package com.kee0kai.thekey.room.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kee0kai.thekey.room.dao.SettingDao;

@Entity(tableName = SettingDao.TABLE_NAME)
public class SettingPairEntry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    public long id;

    @ColumnInfo(name = "value")
    public String value;


    public SettingPairEntry(long id, String value) {
        this.id = id;
        this.value = value;
    }
}
