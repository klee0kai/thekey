package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw

@JniMirror
class CryptStorageEngine(
    val storagePath: String
) {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun isLogined(): Int

    external fun login(passw: String)

    external fun unlogin()

    external fun notes(): Array<DecryptedNote>

    external fun note(notePtr: Long): DecryptedNote

    external fun saveNote(decryptedNote: DecryptedNote): Int

    external fun removeNote(notePt: Long): Int

    external fun getGenPassw(ptNote: Long): DecryptedPassw

    external fun generateNewPassw(len: Int, genPasswEncoding: Int): String

}