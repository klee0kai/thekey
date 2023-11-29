package com.github.klee0kai.thekey.app.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = SettingDao.TABLE_NAME)
data class SettingPairEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    val id: Int,

    @ColumnInfo(name = "value")
    val value: String,
) : Parcelable