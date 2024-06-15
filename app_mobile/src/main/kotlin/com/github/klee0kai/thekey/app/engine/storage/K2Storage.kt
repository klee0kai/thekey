package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage

@JniMirror
class K2Storage(
    val storagePath: String,
    val fileDescriptor: Int? = null,
) : CryptStorage {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external override fun info(): Storage

    external override fun login(passw: String)

    external override fun unlogin()

    external override fun colorGroups(info: Boolean): Array<DecryptedColorGroup>

    external override fun saveColorGroup(group: DecryptedColorGroup): DecryptedColorGroup?

    external override fun removeColorGroup(colorGroupId: Long): Int

    external override fun setNotesGroup(notePtrs: Array<Long>, groupId: Long): Int

    external override fun notes(info: Boolean): Array<DecryptedNote>

    external override fun note(notePtr: Long): DecryptedNote

    external override fun saveNote(
        decryptedNote: DecryptedNote,
        setAll: Boolean,
    ): Int

    external override fun removeNote(notePt: Long): Int

    external override fun otpNotes(info: Boolean): Array<DecryptedOtpNote>

    external override fun otpNote(notePtr: Long): DecryptedOtpNote

    external override fun otpNoteFromUrl(url: String): DecryptedOtpNote?

    external override fun saveOtpNote(decryptedNote: DecryptedOtpNote, setAll: Boolean): Int

    external override fun removeOtpNote(notePt: Long): Int

    external override fun setOtpNotesGroup(notePtrs: Array<Long>, groupId: Long): Int

    external override fun generateNewPassw(params: GenPasswParams): String

    external override fun lastGeneratedPassw(): String

    external override fun genHistory(info: Boolean): Array<DecryptedPassw>

    external override fun getGenPassw(ptNote: Long): DecryptedPassw

}