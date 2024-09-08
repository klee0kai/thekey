package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.basemodel.removeDoublesById
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.otpNotes
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PredefinedNoteGroupsInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.predefinedNoteGroupsRepository(identifier)

    val predefinedGroups = flow {
        val precreated = ColorGroup.otpNotes()
        rep().allPredefinedGroups
            .map { list ->
                (list + precreated)
                    .removeDoublesById()
                    .filter { !it.isRemoved }
            }
            .collect(this)
    }.flowOn(DI.defaultDispatcher())

    val otpNoteGroup = flow {
        rep().allPredefinedGroups.map { list ->
            val precreated = ColorGroup.otpNotes()
            val saved = list.firstOrNull { colorGroup -> colorGroup.id == precreated.id }
            when {
                saved == null -> precreated
                !saved.isRemoved -> saved
                else -> precreated.copy(isRemoved = true)
            }
        }.collect(this)
    }

    fun setColorGroup(
        colorGroup: ColorGroup,
    ) = scope.launch {
        rep().setColorGroup(colorGroup).join()
    }

    fun deleteColorGroup(
        id: Long,
    ) = scope.launch {
        rep().deleteColorGroup(id).join()
    }

}