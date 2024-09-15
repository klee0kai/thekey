package com.github.klee0kai.thekey.app.engine.storage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.CreateStorageConfig
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.engine.model.TwinsCollection

@JniMirror
class K1Storage(
    /**
     * storage identifier unic for open reason and path
     */
    val engineIdentifier: String,
    val storagePath: String,
    val fileDescriptor: Int? = null,
) : CryptStorage {

    init {
        NativeLibLoader.loadIfNeed()
    }

    @Synchronized
    external override fun info(): Storage

    @Synchronized
    external override fun login(
        passw: String,
        createConfig: CreateStorageConfig,
    )

    @Synchronized
    external override fun unlogin()

    @Synchronized
    external override fun logoutAll()

    override fun colorGroups(info: Boolean): Array<DecryptedColorGroup> = emptyArray()

    override fun saveColorGroup(group: DecryptedColorGroup): DecryptedColorGroup? = null

    override fun removeColorGroup(colorGroupId: Long): Int = -1

    override fun setNotesGroup(notePtrs: Array<Long>, groupId: Long): Int = -1

    @Synchronized
    external override fun notes(info: Boolean): Array<DecryptedNote>

    @Synchronized
    external override fun note(notePtr: Long): DecryptedNote

    @Synchronized
    external override fun saveNote(decryptedNote: DecryptedNote, setAll: Boolean): Int

    override fun moveNote(notePt: Long, targetEngineIdentifier: String): Int = -1

    @Synchronized
    external override fun removeNote(notePt: Long): Int

    override fun otpNotes(info: Boolean): Array<DecryptedOtpNote> = emptyArray()

    override fun otpNote(notePtr: Long, increment: Boolean): DecryptedOtpNote = DecryptedOtpNote()

    override fun otpNoteFromUrl(url: String): DecryptedOtpNote? = null

    override fun saveOtpNote(decryptedNote: DecryptedOtpNote, setAll: Boolean): Int = -1

    override fun removeOtpNote(notePt: Long): Int = -1

    override fun setOtpNotesGroup(notePtrs: Array<Long>, groupId: Long): Int = -1

    override fun moveOtpNote(notePt: Long, targetEngineIdentifier: String): Int = -1

    @Synchronized
    external override fun generateNewPassw(params: GenPasswParams): String

    @Synchronized
    external override fun lastGeneratedPassw(): String

    @Synchronized
    external override fun genHistory(info: Boolean): Array<DecryptedPassw>

    @Synchronized
    external override fun getGenPassw(ptNote: Long): DecryptedPassw

    override fun removeHist(histPt: Long): Int = -1

    override fun removeOldHist(oldestTimeSec: Long): Int = -1

    override fun findTwins(passw: String): TwinsCollection? = null


}