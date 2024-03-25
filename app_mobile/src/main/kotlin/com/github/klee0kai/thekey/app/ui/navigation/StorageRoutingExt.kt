package com.github.klee0kai.thekey.app.ui.navigation

import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination

fun Storage.dest() = StorageDestination(version = version, path = path)

fun Storage.identifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.identifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.dest(selectedPage: Int = 0) =
    StorageDestination(version = version, path = path, selectedPage = selectedPage)

fun StorageIdentifier.dest() = StorageDestination(version = version, path = path)

fun StorageDestination.identifier() =
    StorageIdentifier(version = version, path = path)

fun StorageDestination.note(notePtr: Long = 0) =
    NoteDestination(storageVersion = version, path = path, notePtr = notePtr)

fun StorageDestination.createNote(prefilled: DecryptedNote) =
    NoteDestination(storageVersion = version, path = path, prefilled = prefilled)

fun StorageDestination.genHist() =
    GenHistDestination(storageVersion = version, path = path)

fun GenHistDestination.storage() =
    StorageIdentifier(version = storageVersion, path = path)

fun GenHistDestination.storageIdentifier() =
    StorageIdentifier(version = storageVersion, path = path)

fun StorageIdentifier.noteDest(notePtr: Long = 0) =
    NoteIdentifier(storageVersion = version, storagePath = path, notePtr = notePtr)

fun NoteIdentifier.storage() =
    StorageIdentifier(version = storageVersion, path = storagePath)


fun NoteDestination.toIdentifier() =
    NoteIdentifier(storageVersion = storageVersion, storagePath = path, notePtr = notePtr)