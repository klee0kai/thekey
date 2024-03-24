package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage

interface CryptStorage {

    fun info(): Storage

    fun login(passw: String)

    fun unlogin()

    fun notes(): Array<DecryptedNote>

    fun note(notePtr: Long): DecryptedNote

    fun saveNote(decryptedNote: DecryptedNote): Int

    fun removeNote(notePt: Long): Int

    fun getGenPassw(ptNote: Long): DecryptedPassw

    fun lastGeneratedPassw(): String

    fun generateNewPassw(params: GenPasswParams): String

}