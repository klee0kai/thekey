package com.github.klee0kai.thekey.core.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.github.klee0kai.thekey.core.data.room.dao.StorageFilesDao
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = StorageFilesDao.TABLE_NAME)
data class StorageFileEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "path")
    val path: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "color_group")
    val coloredGroupId: Long = 0,
) : Parcelable


