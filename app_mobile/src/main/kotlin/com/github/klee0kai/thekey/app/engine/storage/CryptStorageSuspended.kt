package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
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

    suspend fun info(): Storage = engineRun { info() }

    suspend fun login(passw: String) = engineRun { login(passw) }

    suspend fun unlogin() = engineRun { unlogin() }

    suspend fun colorGroups(info: Boolean = false): Array<DecryptedColorGroup> = engineRun { colorGroups(info) }

    suspend fun saveColorGroup(group: DecryptedColorGroup): Int = engineRun { saveColorGroup(group) }

    suspend fun removeColorGroup(colorGroupId: Long): Int = engineRun { removeColorGroup(colorGroupId) }

    suspend fun notes(info: Boolean = false): Array<DecryptedNote> = engineRun { notes(info) }

    suspend fun note(notePtr: Long): DecryptedNote = engineRun { note(notePtr) }

    suspend fun saveNote(
        note: DecryptedNote,
        setAll: Boolean = false,
    ): Int = engineRun { saveNote(note, setAll = setAll) }

    suspend fun removeNote(noteptr: Long): Int = engineRun { removeNote(noteptr) }

    suspend fun generateNewPassw(params: GenPasswParams): String = engineRun { generateNewPassw(params) }

    suspend fun genHistory(): Array<DecryptedPassw> = engineRun { genHistory() }

    suspend fun lastGeneratedPassw(): String = engineRun { lastGeneratedPassw() }

    suspend fun getGenPassw(ptNote: Long): DecryptedPassw = engineRun { getGenPassw(ptNote) }

    private suspend fun <T> engineRun(block: suspend CryptStorage.() -> T): T = withContext(dispatcher) {
        _engine()!!.block()
    }

}