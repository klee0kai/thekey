package com.github.klee0kai.thekey.app.ui.navigation

import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination

fun Storage.toStorageDest() = StorageDestination(version = version, path = path)

fun Storage.toStorageIdentifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.toStorageIdentifier() = StorageIdentifier(version = version, path = path)

fun ColoredStorage.toStorageDest(selectedPage: Int = 0) =
    StorageDestination(version = version, path = path, selectedPage = selectedPage)

fun StorageIdentifier.toDestination() = StorageDestination(version = version, path = path)

fun StorageDestination.toStorageIdentifier() =
    StorageIdentifier(version = version, path = path)

fun StorageDestination.noteDestination(notePtr: Long = 0) =
    NoteDestination(version = version, path = path, notePtr = notePtr)