package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage
import kotlinx.coroutines.withContext

class CryptStorageSuspendedImpl(
    storageIdentifier: StorageIdentifier,
) : CryptStorageSuspended {

    private val _engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val dispatcher = DI.jniDispatcher()

    override suspend fun info(): Storage = withContext(dispatcher) {
        engine().info()
    }

    override suspend fun login(passw: String) = withContext(dispatcher) {
        engine().login(passw)
    }

    override suspend fun unlogin() = withContext(dispatcher) {
        engine().unlogin()
    }

    override suspend fun notes(info: Boolean): Array<DecryptedNote> = withContext(dispatcher) {
        engine().notes(info)
    }

    override suspend fun note(notePtr: Long): DecryptedNote = withContext(dispatcher) {
        engine().note(notePtr)
    }

    override suspend fun saveNote(decryptedNote: DecryptedNote): Int = withContext(dispatcher) {
        engine().saveNote(decryptedNote)
    }

    override suspend fun removeNote(noteptr: Long): Int = withContext(dispatcher) {
        engine().removeNote(noteptr)
    }

    override suspend fun generateNewPassw(params: GenPasswParams): String = withContext(dispatcher) {
        engine().generateNewPassw(params)
    }

    override suspend fun genHistory(): Array<DecryptedPassw> = withContext(dispatcher) {
        engine().genHistory()
    }

    override suspend fun lastGeneratedPassw(): String = withContext(dispatcher) {
        engine().lastGeneratedPassw()
    }

    override suspend fun getGenPassw(ptNote: Long): DecryptedPassw = withContext(dispatcher) {
        engine().getGenPassw(ptNote)
    }

    private suspend fun engine() = _engine()!!

}