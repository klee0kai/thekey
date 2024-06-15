package com.github.klee0kai.thekey.app.data.mapping

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.data.room.entry.StorageFileEntry
import com.github.klee0kai.thekey.core.domain.ColorGroup


fun StorageFileEntry.toStorage(): Storage = this.run {
    Storage(path, name, description)
}

fun Storage.toStorageEntry(
    id: Long? = null
): StorageFileEntry = this.run {
    StorageFileEntry(id = id ?: 0, path = path, name = name, description = description)
}

fun Storage.toColoredStorage(): ColoredStorage = this.run {
    ColoredStorage(path = path, name = name, description = description, version = version)
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
