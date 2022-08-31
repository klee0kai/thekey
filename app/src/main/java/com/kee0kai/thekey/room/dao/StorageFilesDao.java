package com.kee0kai.thekey.room.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kee0kai.thekey.room.model.StorageFileEntry;

@Dao
public interface StorageFilesDao {

    String TABLE_NAME = "storagefiles";

    @Insert(onConflict = REPLACE)
    void insert(StorageFileEntry entry);

    @Query("SELECT COUNT(*)>0 FROM " + StorageFilesDao.TABLE_NAME + " WHERE path = :file")
    boolean exist(String file);

    @Query("SELECT COUNT(*) FROM " + StorageFilesDao.TABLE_NAME)
    int count();

    @Query("DELETE  FROM " + StorageFilesDao.TABLE_NAME + " WHERE path = :file")
    void delete(String file);

    @Query("DELETE FROM " + StorageFilesDao.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + StorageFilesDao.TABLE_NAME)
    StorageFileEntry[] get();

    @Query("SELECT * FROM " + StorageFilesDao.TABLE_NAME + " WHERE path=:path")
    StorageFileEntry get(String path);

}
