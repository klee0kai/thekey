package com.github.klee0kai.thekey.core.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.klee0kai.thekey.core.data.room.entry.NoteColorGroupEntry

@Dao
interface NoteColorGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entry: NoteColorGroupEntry): Long

    @Query("SELECT * FROM $TABLE_NAME WHERE st_path = :storagePath ")
    fun getAll(storagePath:String): List<NoteColorGroupEntry>

    @Query("DELETE FROM $TABLE_NAME WHERE st_path = :storagePath AND predefinedId = :predefinedGroupId")
    fun delete(storagePath:String, predefinedGroupId: Long)

    companion object {
        const val TABLE_NAME = "note_color_group"
    }
}