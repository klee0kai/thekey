package com.github.klee0kai.thekey.core.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.klee0kai.thekey.core.data.room.entry.SettingPairEntry

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entry: SettingPairEntry)

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    operator fun get(id: Int): SettingPairEntry?

    companion object {
        const val TABLE_NAME = "settting_pairs"
    }
}