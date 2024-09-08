package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.otpNotes
import kotlinx.coroutines.flow.flow
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
            .map {
                var list = it
                if (list.none { group -> group.id == precreated.id }) {
                    list = list + precreated
                }
                list
            }
            .collect(this)
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