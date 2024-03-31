package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
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

    external override fun info(): Storage

    external override fun login(passw: String)

    external override fun unlogin()

    external override fun colorGroups(info: Boolean): Array<DecryptedColorGroup>

    external override fun saveColorGroup(group: DecryptedColorGroup): Int

    external override fun removeColorGroup(colorGroupId: Long): Int

    external override fun notes(info: Boolean): Array<DecryptedNote>

    external override fun note(notePtr: Long): DecryptedNote

    external override fun saveNote(
        decryptedNote: DecryptedNote,
        setAll: Boolean,
    ): Int

    external override fun removeNote(notePt: Long): Int

    external override fun generateNewPassw(params: GenPasswParams): String

    external override fun lastGeneratedPassw(): String

    external override fun genHistory(): Array<DecryptedPassw>

    external override fun getGenPassw(ptNote: Long): DecryptedPassw

}