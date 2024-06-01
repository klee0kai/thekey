package com.github.klee0kai.thekey.app.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.klee0kai.thekey.app.data.room.entry.StorageFileEntry

@Dao
interface StorageFilesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(entry: StorageFileEntry)

    @Query("SELECT COUNT(*)>0 FROM $TABLE_NAME WHERE path = :file")
    fun exist(file: String?): Boolean

    @Query("SELECT COUNT(*) FROM $TABLE_NAME")
    fun count(): Int

    @Query("DELETE  FROM $TABLE_NAME WHERE path = :file")
    fun delete(file: String?)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): List<StorageFileEntry>

    @Query("SELECT * FROM $TABLE_NAME WHERE path=:path")
    fun get(path: String?): StorageFileEntry?

    companion object {
        const val TABLE_NAME = "storagefiles"
    }
}