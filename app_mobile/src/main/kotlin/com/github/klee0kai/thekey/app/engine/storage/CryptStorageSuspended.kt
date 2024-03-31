package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage

interface CryptStorageSuspended {

    suspend fun info(): Storage

    suspend fun login(passw: String)

    suspend fun unlogin()

    suspend fun notes(info: Boolean = false): Array<DecryptedNote>

    suspend fun note(notePtr: Long): DecryptedNote

    suspend fun saveNote(decryptedNote: DecryptedNote): Int

    suspend fun removeNote(notePt: Long): Int

    suspend fun generateNewPassw(params: GenPasswParams): String

    suspend fun genHistory(): Array<DecryptedPassw>

    suspend fun lastGeneratedPassw(): String

    suspend fun getGenPassw(ptNote: Long): DecryptedPassw

}