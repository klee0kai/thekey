package com.github.klee0kai.thekey.app.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.klee0kai.thekey.app.data.room.entry.ColorGroupEntry

@Dao
interface ColorGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entry: ColorGroupEntry)

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    operator fun get(id: Int): ColorGroupEntry?

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): List<ColorGroupEntry>

    companion object {
        const val TABLE_NAME = "color_group"
    }
}