package com.github.klee0kai.thekey.core.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.klee0kai.thekey.core.data.room.entry.ColorGroupEntry

@Dao
interface ColorGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entry: ColorGroupEntry): Long

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    operator fun get(id: Long): ColorGroupEntry?

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): List<ColorGroupEntry>

    @Query("DELETE  FROM $TABLE_NAME WHERE id = :id")
    fun delete(id: Long)

    companion object {
        const val TABLE_NAME = "color_group"
    }
}