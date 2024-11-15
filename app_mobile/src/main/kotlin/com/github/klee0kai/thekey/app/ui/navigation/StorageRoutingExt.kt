package com.github.klee0kai.thekey.app.ui.navigation

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.HistDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage

fun Storage.dest() = StorageDestination(version = version, path = path)

fun Storage.identifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.identifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.dest(selectedPage: Int = 0) =
    StorageDestination(version = version, path = path, selectedPage = selectedPage)

fun StorageIdentifier.dest() = StorageDestination(version = version, path = path)

fun StorageDestination.identifier() =
    StorageIdentifier(version = version, path = path)

fun StorageDestination.note(
    notePtr: Long = 0,
) = NoteDialogDestination(
    storageVersion = version,
    path = path,
    notePtr = notePtr,
)

fun StorageDestination.otpNote(
    otpNotePtr: Long = 0,
) = NoteDialogDestination(
    storageVersion = version,
    path = path,
    otpNotePtr = otpNotePtr,
)

fun StorageDestination.editNote(notePtr: Long = 0) =
    EditNoteDestination(
        storageVersion = version,
        path = path,
        note = DecryptedNote(ptnote = notePtr)
    )


fun StorageDestination.editOtpNote(
    otpNotePtr: Long = 0
) = EditNoteDestination(
    storageVersion = version,
    path = path,
    otpNote = DecryptedOtpNote(ptnote = otpNotePtr),
)

fun StorageIdentifier.editNoteDest(
    prefilledNote: DecryptedNote? = null,
    prefilledOtpNote: DecryptedOtpNote? = null,
    isIgnoreDelete: Boolean = false,
) = EditNoteDestination(
    storageVersion = version,
    path = path,
    note = prefilledNote,
    otpNote = prefilledOtpNote,
    tab = if (prefilledOtpNote != null) EditTabs.Otp else EditTabs.Account,
    isIgnoreRemove = isIgnoreDelete,
)

fun NoteIdentifier.editNoteDest(
) = EditNoteDestination(
    path = storagePath,
    storageVersion = storageVersion,
    note = if (notePtr != 0L) DecryptedNote(ptnote = notePtr) else null,
    otpNote = if (otpNotePtr != 0L) DecryptedOtpNote(ptnote = otpNotePtr) else null,
)


fun StorageDestination.genHist() =
    HistDestination(storageVersion = version, path = path)

fun HistDestination.storage() =
    StorageIdentifier(version = storageVersion, path = path)

fun HistDestination.storageIdentifier() =
    StorageIdentifier(version = storageVersion, path = path)

fun HistDestination.noteIdentifier(
) = notePtr?.let {
    NoteIdentifier(
        storageVersion = storageVersion,
        storagePath = path,
        notePtr = notePtr,
    )
}

fun NoteIdentifier.histDest(
) = HistDestination(
    storageVersion = storageVersion,
    path = storagePath,
    notePtr = notePtr,
)


fun StorageIdentifier.noteDest(notePtr: Long = 0) =
    NoteIdentifier(storageVersion = version, storagePath = path, notePtr = notePtr)

fun NoteIdentifier.storage() =
    StorageIdentifier(version = storageVersion, path = storagePath)

fun EditNoteDestination.identifier() =
    NoteIdentifier(
        storageVersion = storageVersion,
        storagePath = path,
        notePtr = note?.ptnote ?: 0L,
        otpNotePtr = otpNote?.ptnote ?: 0L,
    )

fun NoteDialogDestination.identifier() =
    NoteIdentifier(
        storageVersion = storageVersion,
        storagePath = path,
        notePtr = notePtr ?: 0L,
        otpNotePtr = otpNotePtr ?: 0L,
    )

fun EditStorageGroupDestination.identifier() = StorageGroupIdentifier(
    groupId = groupId
)


fun StorageDestination.createGroup() =
    EditNoteGroupDestination(StorageIdentifier(path, version))

fun StorageDestination.editGroup(groupId: Long) =
    EditNoteGroupDestination(StorageIdentifier(path, version), groupId = groupId)

fun EditNoteGroupDestination.identifier() =
    NoteGroupIdentifier(storageIdentifier = storageIdentifier, groupId = groupId)