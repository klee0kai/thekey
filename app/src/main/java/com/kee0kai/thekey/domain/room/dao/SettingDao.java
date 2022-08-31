package com.kee0kai.thekey.domain.room.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kee0kai.thekey.domain.room.model.SettingPairEntry;

@Dao
public interface SettingDao {

    String TABLE_NAME = "settting_pairs";

    @Insert(onConflict = REPLACE)
    void update(SettingPairEntry entry);

    @Query("SELECT * FROM " + SettingDao.TABLE_NAME + " WHERE id = :id")
    SettingPairEntry get(long id);

}
