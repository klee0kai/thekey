package com.github.klee0kai.thekey.core.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.klee0kai.thekey.core.data.room.dao.NoteColorGroupDao
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = NoteColorGroupDao.TABLE_NAME)
data class NoteColorGroupEntry(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(index = true, name = "id")
    val id: String,

    @ColumnInfo(name = "predefinedId")
    val predefinedGroupId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "color_group")
    val colorGroup: Int = 0,

    @ColumnInfo(name = "st_path")
    val storagePath: String = "",

    @ColumnInfo(name = "removed")
    val isRemoved: Boolean = false,
) : Parcelable


fun NoteColorGroupEntry.toColorGroup() =
    ColorGroup(
        id = predefinedGroupId,
        name = name,
        keyColor = KeyColor.entries
            .getOrElse(colorGroup) {
                KeyColor.TURQUOISE
            },
        isRemoved = isRemoved,
    )


fun ColorGroup.toNoteColorGroupEntry(
    storagePath: String = "",
) = NoteColorGroupEntry(
    id = "$storagePath - $id",
    predefinedGroupId = this.id,
    name = name,
    colorGroup = keyColor.ordinal,
    storagePath = storagePath,
    isRemoved = isRemoved,
)