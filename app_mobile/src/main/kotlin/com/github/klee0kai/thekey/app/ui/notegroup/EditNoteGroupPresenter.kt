package com.github.klee0kai.thekey.app.ui.notegroup

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.MutableStateFlow

class EditNoteGroupPresenter(id: NoteGroupIdentifier) {

    private val engine = DI.cryptStorageEngineSafeLazy(id.storageIdentifier)
    private val scope = DI.defaultThreadScope()

    val selectedKeyColor = MutableStateFlow(KeyColor.NOCOLOR)
    val name = MutableStateFlow("")



    fun save() = scope.launchLatest("save") {


    }


}