package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage
import kotlinx.coroutines.withContext

class CryptStorageSuspended(
    storageIdentifier: StorageIdentifier,
) {

    private val _engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val dispatcher = DI.jniDispatcher()

    suspend fun info(): Storage = withContext(dispatcher) {
        engine().info()
    }

    suspend fun login(passw: String) = withContext(dispatcher) {
        engine().login(passw)
    }

    suspend fun unlogin() = withContext(dispatcher) {
        engine().unlogin()
    }

    suspend fun notes(info: Boolean = false): Array<DecryptedNote> = withContext(dispatcher) {
        engine().notes(info)
    }

    suspend fun note(notePtr: Long): DecryptedNote = withContext(dispatcher) {
        engine().note(notePtr)
    }

    suspend fun saveNote(decryptedNote: DecryptedNote): Int = withContext(dispatcher) {
        engine().saveNote(decryptedNote)
    }

    suspend fun removeNote(noteptr: Long): Int = withContext(dispatcher) {
        engine().removeNote(noteptr)
    }

    suspend fun generateNewPassw(params: GenPasswParams): String = withContext(dispatcher) {
        engine().generateNewPassw(params)
    }

    suspend fun genHistory(): Array<DecryptedPassw> = withContext(dispatcher) {
        engine().genHistory()
    }

    suspend fun lastGeneratedPassw(): String = withContext(dispatcher) {
        engine().lastGeneratedPassw()
    }

    suspend fun getGenPassw(ptNote: Long): DecryptedPassw = withContext(dispatcher) {
        engine().getGenPassw(ptNote)
    }

    private suspend fun engine() =
        _engine()!!

}