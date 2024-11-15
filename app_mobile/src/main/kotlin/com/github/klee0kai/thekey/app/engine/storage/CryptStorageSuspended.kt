package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.CreateStorageConfig
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.engine.model.TwinsCollection
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CryptStorageSuspended(
    storageIdentifier: StorageIdentifier,
) {

    private val _engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val fileMutex = DI.fileMutex(FileIdentifier(storageIdentifier.path))
    private val dispatcher = DI.jniDispatcher()

    suspend fun info(): Storage = engineRunRead { info() }

    suspend fun login(
        passw: String,
        createStorageConfig: CreateStorageConfig,
    ) = engineRunRead {
        login(passw, createStorageConfig)
    }

    suspend fun unlogin() = engineRunRead { unlogin() }

    suspend fun logoutAll() = engineRunRead { logoutAll() }

    /* color groups  */
    suspend fun colorGroups(info: Boolean = false): Array<DecryptedColorGroup> =
        engineRunRead { colorGroups(info) }

    suspend fun saveColorGroup(group: DecryptedColorGroup): DecryptedColorGroup? =
        engineRunWrite { saveColorGroup(group) }

    suspend fun removeColorGroup(colorGroupId: Long): Int =
        engineRunWrite { removeColorGroup(colorGroupId) }

    /* notes  */

    suspend fun notes(info: Boolean = false): Array<DecryptedNote> = engineRunRead { notes(info) }

    suspend fun note(notePtr: Long): DecryptedNote = engineRunRead { note(notePtr) }

    suspend fun saveNote(
        note: DecryptedNote,
        setAll: Boolean = false,
    ): Int = engineRunWrite { saveNote(note, setAll = setAll) }

    suspend fun setNotesGroup(
        notePtrs: Array<Long>,
        groupId: Long,
    ): Int = engineRunWrite { setNotesGroup(notePtrs, groupId) }

    suspend fun removeNote(noteptr: Long): Int = engineRunWrite { removeNote(noteptr) }

    suspend fun moveNote(
        notePt: Long,
        targetStoragePath: String,
        targetEngineIdentifier: String,
    ): Int {
        return DI.fileMutex(FileIdentifier(targetStoragePath))
            .withWriteLock {
                engineRunRead {
                    moveNote(notePt, targetEngineIdentifier)
                }
            }
    }


    /*  otp notes */

    suspend fun otpNotes(info: Boolean = false): Array<DecryptedOtpNote> =
        engineRunRead { otpNotes(info) }

    suspend fun otpNote(
        notePtr: Long,
        increment: Boolean = false,
    ): DecryptedOtpNote = engineRunRead {
        otpNote(notePtr, increment)
    }

    suspend fun otpNoteFromUrl(url: String): DecryptedOtpNote? =
        engineRunRead { otpNoteFromUrl(url) }

    suspend fun saveOtpNote(decryptedNote: DecryptedOtpNote, setAll: Boolean = false): Int =
        engineRunWrite { saveOtpNote(decryptedNote, setAll) }

    suspend fun removeOtpNote(notePt: Long): Int = engineRunWrite { removeOtpNote(notePt) }

    suspend fun moveOtpNote(
        notePt: Long,
        targetStoragePath: String,
        targetEngineIdentifier: String,
    ): Int {
        return DI.fileMutex(FileIdentifier(targetStoragePath))
            .withWriteLock {
                engineRunRead {
                    moveOtpNote(notePt, targetEngineIdentifier)
                }
            }
    }

    suspend fun setOtpNotesGroup(
        notePtrs: Array<Long>,
        groupId: Long,
    ): Int = engineRunWrite {
        setOtpNotesGroup(notePtrs, groupId)
    }

    /* gen passw  */

    suspend fun generateNewPassw(params: GenPasswParams): String =
        engineRunWrite { generateNewPassw(params) }

    suspend fun genHistory(info: Boolean = false): Array<DecryptedPassw> =
        engineRunRead { genHistory(info) }

    suspend fun lastGeneratedPassw(): String = engineRunRead { lastGeneratedPassw() }

    suspend fun removeHist(histPtr: Long): Int = engineRunRead { removeHist(histPtr) }

    suspend fun removeOldHist(
        oldestTimeSec: Long,
    ): Int = engineRunWrite {
        removeOldHist(oldestTimeSec)
    }

    suspend fun getGenPassw(
        ptNote: Long,
    ): DecryptedPassw = engineRunRead {
        getGenPassw(ptNote)
    }

    suspend fun findTwins(
        passw: String,
    ): TwinsCollection? = engineRunRead {
        findTwins(passw)
    }

    private suspend fun <T> engineRunRead(block: suspend CryptStorage.() -> T): T =
        withContext(dispatcher) {
            fileMutex.withReadLock {
                delay(DebugConfigs.engineDelay.readDelay)
                _engine().block()
            }
        }

    private suspend fun <T> engineRunWrite(block: suspend CryptStorage.() -> T): T =
        withContext(dispatcher) {
            fileMutex.withWriteLock {
                delay(DebugConfigs.engineDelay.writeDelay)
                _engine().block()
            }
        }


}