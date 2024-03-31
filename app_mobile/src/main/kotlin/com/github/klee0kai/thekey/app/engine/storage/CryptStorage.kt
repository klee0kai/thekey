package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage

interface CryptStorage {

    fun info(): Storage

    fun login(passw: String)

    fun unlogin()

    fun colorGroups(info: Boolean = false): Array<DecryptedColorGroup>

    fun saveColorGroup(group: DecryptedColorGroup): Int

    fun removeColorGroup(colorGroupId: Long): Int

    fun notes(info: Boolean = false): Array<DecryptedNote>

    fun note(notePtr: Long): DecryptedNote

    fun saveNote(decryptedNote: DecryptedNote): Int

    fun removeNote(notePt: Long): Int

    fun generateNewPassw(params: GenPasswParams): String

    fun genHistory(): Array<DecryptedPassw>

    fun lastGeneratedPassw(): String

    fun getGenPassw(ptNote: Long): DecryptedPassw

}