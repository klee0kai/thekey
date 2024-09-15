package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.thekey.app.engine.model.CreateStorageConfig
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.engine.model.TwinsCollection

interface CryptStorage {

    fun info(): Storage

    fun login(
        passw: String,
        createConfig: CreateStorageConfig,
    )

    fun unlogin()

    fun logoutAll()

    fun colorGroups(info: Boolean = false): Array<DecryptedColorGroup>

    fun saveColorGroup(group: DecryptedColorGroup): DecryptedColorGroup?

    fun removeColorGroup(colorGroupId: Long): Int

    fun setNotesGroup(notePtrs: Array<Long>, groupId: Long): Int

    fun notes(info: Boolean = false): Array<DecryptedNote>

    fun note(notePtr: Long): DecryptedNote

    fun saveNote(decryptedNote: DecryptedNote, setAll: Boolean = false): Int

    fun moveNote(notePt: Long, targetStorageIdentifier: String): Int

    fun removeNote(notePt: Long): Int

    fun otpNotes(info: Boolean = false): Array<DecryptedOtpNote>

    fun otpNote(notePtr: Long, increment: Boolean = false): DecryptedOtpNote

    fun otpNoteFromUrl(url: String): DecryptedOtpNote?

    fun setOtpNotesGroup(notePtrs: Array<Long>, groupId: Long): Int

    fun saveOtpNote(decryptedNote: DecryptedOtpNote, setAll: Boolean = false): Int

    fun moveOtpNote(notePt: Long, targetStorageIdentifier: String): Int

    fun removeOtpNote(notePt: Long): Int

    fun generateNewPassw(params: GenPasswParams): String

    fun genHistory(info: Boolean): Array<DecryptedPassw>

    fun removeHist(histPt: Long): Int

    fun removeOldHist(oldestTimeSec: Long): Int

    fun lastGeneratedPassw(): String

    fun getGenPassw(ptNote: Long): DecryptedPassw

    fun findTwins(passw: String): TwinsCollection?

}