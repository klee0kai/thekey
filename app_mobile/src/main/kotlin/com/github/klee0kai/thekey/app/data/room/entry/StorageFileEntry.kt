package com.github.klee0kai.thekey.app.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = StorageFilesDao.TABLE_NAME)
data class StorageFileEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "path")
    var path: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,
) : Parcelable