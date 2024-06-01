package com.github.klee0kai.thekey.app.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.klee0kai.thekey.app.data.room.dao.StorageFilesDao
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.domain.ColorGroup
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


fun StorageFileEntry.toStorage(): Storage = this.run {
    Storage(path, name, description)
}

fun Storage.toStorageEntry(
    id: Long? = null
): StorageFileEntry = this.run {
    StorageFileEntry(id = id ?: 0, path = path, name = name, description = description)
}

fun StorageFileEntry.toColoredStorage(): ColoredStorage = this.run {
    ColoredStorage(path = path, name = name, description = description, colorGroup = ColorGroup(id = coloredGroupId))
}


fun ColoredStorage.toStorageEntry(
    id: Long? = null
): StorageFileEntry = this.run {
    StorageFileEntry(
        id = id ?: 0,
        path = path,
        name = name,
        description = description,
        coloredGroupId = colorGroup?.id ?: 0
    )
}

