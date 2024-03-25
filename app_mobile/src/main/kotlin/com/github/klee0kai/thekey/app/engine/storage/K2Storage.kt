package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage

@JniMirror
class K2Storage(
    val storagePath: String
) : CryptStorage {

    init {
        NativeLibLoader.loadIfNeed()
    }

    @Synchronized
    external override fun info(): Storage

    @Synchronized
    external override fun login(passw: String)

    @Synchronized
    external override fun unlogin()

    @Synchronized
    external override fun notes(info: Boolean): Array<DecryptedNote>

    @Synchronized
    external override fun note(notePtr: Long): DecryptedNote

    @Synchronized
    external override fun saveNote(decryptedNote: DecryptedNote): Int

    @Synchronized
    external override fun removeNote(notePt: Long): Int

    @Synchronized
    external override fun generateNewPassw(params: GenPasswParams): String

    @Synchronized
    external override fun lastGeneratedPassw(): String

    @Synchronized
    external override fun genHistory(): Array<DecryptedPassw>

    @Synchronized
    external override fun getGenPassw(ptNote: Long): DecryptedPassw

}