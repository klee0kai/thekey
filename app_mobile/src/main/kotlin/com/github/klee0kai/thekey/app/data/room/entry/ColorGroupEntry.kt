package com.github.klee0kai.thekey.app.data.room.entry

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.klee0kai.thekey.app.data.room.dao.ColorGroupDao
import com.github.klee0kai.thekey.app.model.ColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = ColorGroupDao.TABLE_NAME)
data class ColorGroupEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_group")
    val colorGroup: Int,
) : Parcelable


fun ColorGroupEntry.toColorGroup() =
    ColorGroup(
        name = name,
        keyColor = KeyColor.entries
            .getOrElse(colorGroup) {
                KeyColor.TURQUOISE
            }
    )


fun ColorGroup.toColorGroupEntry(
    id: Long? = null
) =
    ColorGroupEntry(
        id = id ?: 0L,
        name = name,
        colorGroup = keyColor.ordinal
    )