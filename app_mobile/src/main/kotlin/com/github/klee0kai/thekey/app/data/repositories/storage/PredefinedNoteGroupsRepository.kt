package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.data.room.entry.toColorGroup
import com.github.klee0kai.thekey.core.data.room.entry.toNoteColorGroupEntry
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.utils.coroutine.touch
import com.github.klee0kai.thekey.core.utils.coroutine.touchable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PredefinedNoteGroupsRepository(
    val storageIdentifier: StorageIdentifier = StorageIdentifier(),
) {

    private val noteColorGroupDao = DI.noteColorGroupDaoLazy()
    private val scope = DI.ioThreadScope()

    val allPredefinedGroups = flow<List<ColorGroup>> {
        noteColorGroupDao().getAll(storageIdentifier.path)
            .map { it.toColorGroup() }
            .also { list -> emit(list) }
    }.touchable()

    fun setColorGroup(colorGroup: ColorGroup) = scope.launch {
        noteColorGroupDao().update(
            colorGroup.toNoteColorGroupEntry(
                storagePath = storageIdentifier.path,
            )
        )
        allPredefinedGroups.touch()
    }

    fun deleteColorGroup(id: Long) = scope.launch {
        noteColorGroupDao().update(
            ColorGroup(
                id = id,
                isRemoved = true,
            ).toNoteColorGroupEntry(storageIdentifier.path)
        )
        allPredefinedGroups.touch()
    }

}