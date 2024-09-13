package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.coloredOtpNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.domain.model.OtpMethod
import com.github.klee0kai.thekey.core.domain.model.findNextUpdateTime
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.util.concurrent.atomic.AtomicBoolean

class OtpNotesRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    val otpNotes = lazyStateFlow(
        init = emptyList<ColoredOtpNote>(),
        defaultArg = false,
        scope = scope,
    ) { force ->
        if (value.isNotEmpty() && !force) return@lazyStateFlow

        if (value.isEmpty()) {
            value = engine().otpNotes()
                .map { it.coloredOtpNote(isLoaded = false) }
        }

        value = engine().otpNotes(info = true)
            .map { it.coloredOtpNote(isLoaded = true) }
    }

    suspend fun otpNote(
        notePtr: Long,
        increment: Boolean = false,
    ) = engine().otpNote(notePtr, increment)

    suspend fun otpNotePinUpdates(
        notePtr: Long,
        increment: Boolean,
    ): Flow<ColoredOtpNote> {
        val incrementUsed = AtomicBoolean(increment)
        return channelFlow {
            scope.launch {
                while (isActive) {
                    val otp = engine().otpNote(
                        notePtr = notePtr,
                        increment = incrementUsed.getAndSet(false)
                    ).coloredOtpNote(isLoaded = true)
                        .findNextUpdateTime()
                    send(otp)
                    when (otp.method) {
                        OtpMethod.OTP,
                        OtpMethod.HOTP -> break

                        OtpMethod.TOTP,
                        OtpMethod.YAOTP -> {
                            val now = System.currentTimeMillis()
                            delay(maxOf(otp.nextUpdateTime - now + 100, 100))
                        }
                    }
                }
            }
            awaitClose()
        }
    }

    suspend fun setOtpNotesGroup(otpNotePtrs: List<Long>, groupId: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            otpNotes.update { list ->
                list.map { otp ->
                    if (otp.id in otpNotePtrs) {
                        otp.copy(group = ColorGroup(id = groupId))
                    } else {
                        otp
                    }
                }
            }
        }
        engine().setOtpNotesGroup(otpNotePtrs.toTypedArray(), groupId)
        otpNotes.touch(true)
    }

    suspend fun saveOtpNote(otpNote: DecryptedOtpNote, setAll: Boolean = false) {
        if (DebugConfigs.isNotesFastUpdate) {
            otpNotes.update { list ->
                list.filter { it.id != otpNote.ptnote } +
                        listOf(otpNote.coloredOtpNote(isLoaded = true))
            }
        }
        engine().saveOtpNote(otpNote, setAll = setAll)
        otpNotes.touch(true)
    }

    suspend fun removeOtpNote(noteptr: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            otpNotes.update { list -> list.filter { it.id != noteptr } }
        }

        engine().removeOtpNote(noteptr)
        otpNotes.touch(true)
    }

    suspend fun otpNoteFromUrl(url: String): DecryptedOtpNote? {
        return engine().otpNoteFromUrl(url)
    }

    suspend fun clearCache() {
        otpNotes.value = emptyList()
    }

}