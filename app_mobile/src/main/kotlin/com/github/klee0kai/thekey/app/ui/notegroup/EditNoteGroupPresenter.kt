package com.github.klee0kai.thekey.app.ui.notegroup

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteGroupPresenter(val groupIdentifier: NoteGroupIdentifier) {

    private val engine = DI.cryptStorageEngineSafeLazy(groupIdentifier.storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    val selectedKeyColor = MutableStateFlow(KeyColor.NOCOLOR)
    val name = MutableStateFlow("")
    val selectedNotes = MutableStateFlow(emptySet<Long>())

    fun init() = scope.launch {
        if (groupIdentifier.groupId == null) return@launch
        val original = engine()?.colorGroups(info = true)
            ?.first { it.id == groupIdentifier.groupId }
            ?: return@launch

        name.value = original.name
        selectedKeyColor.value = KeyColor.entries.getOrElse(original.color) { KeyColor.NOCOLOR }

        selectedNotes.value = engine()?.notes()
            ?.filter { it.colorGroupId == original.id }
            ?.map { it.ptnote }
            ?.toSet()
            ?: emptySet()
    }

    fun noteSelected(selectedNoteId: Long) = scope.launch {
        selectedNotes.update { notes ->
            if (notes.contains(selectedNoteId)) {
                notes.toMutableSet().apply { remove(selectedNoteId) }
            } else {
                notes + setOf(selectedNoteId)
            }
        }
    }

    fun save() = scope.launchLatest("save") {
        if (selectedKeyColor.value == KeyColor.NOCOLOR) {
            router.snack(R.string.select_color)
            return@launchLatest
        }
        engine()?.saveColorGroup(
            DecryptedColorGroup(
                id = groupIdentifier.groupId ?: 0,
                color = selectedKeyColor.value.ordinal,
                name = name.value
            )
        )
        engine()?.notes()
            ?.forEach { note ->
                launch {
                    when {
                        selectedNotes.value.contains(note.ptnote) -> {
                            engine()?.saveNote(
                                note = note.copy(colorGroupId = groupIdentifier.groupId ?: 0),
                                setAll = false,
                            )
                        }

                        note.colorGroupId == groupIdentifier.groupId -> {
                            engine()?.saveNote(
                                note = note.copy(colorGroupId = 0),
                                setAll = false,
                            )
                        }
                    }
                }
            }

        clear()
        router.back()
    }


    fun clear() = scope.launchLatest("clear") {
        selectedKeyColor.value = KeyColor.NOCOLOR
        name.value = ""
        selectedNotes.value = emptySet()
    }

}