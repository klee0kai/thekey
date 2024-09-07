package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.coloredOtpNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.OtpMethod
import com.github.klee0kai.thekey.core.domain.model.findNextUpdateTime
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.collectTo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class OtpNotesRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()
    private val consumers = AtomicInteger(0)
    private val _otpNotes = MutableStateFlow<List<ColoredOtpNote>>(emptyList())
    val otpNotes = channelFlow {
        consumers.incrementAndGet()
        loadOtpNotes()
        _otpNotes.collectTo(this)
        awaitClose { consumers.decrementAndGet() }
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
        _otpNotes.update { list ->
            list.map { otp ->
                if (otp.ptnote in otpNotePtrs) {
                    otp.copy(group = ColorGroup(id = groupId))
                } else {
                    otp
                }
            }
        }
        engine().setOtpNotesGroup(otpNotePtrs.toTypedArray(), groupId)
        loadOtpNotes(force = true)
    }

    suspend fun saveOtpNote(otpNote: DecryptedOtpNote, setAll: Boolean = false) {
        _otpNotes.update { list ->
            list.filter { it.ptnote != otpNote.ptnote } +
                    listOf(otpNote.coloredOtpNote(isLoaded = true))
        }
        engine().saveOtpNote(otpNote, setAll = setAll)
        loadOtpNotes(force = true)
    }

    suspend fun removeOtpNote(noteptr: Long) {
        _otpNotes.update { list -> list.filter { it.ptnote != noteptr } }

        engine().removeOtpNote(noteptr)
        loadOtpNotes(force = true)
    }

    suspend fun otpNoteFromUrl(url: String): DecryptedOtpNote? {
        return engine().otpNoteFromUrl(url)
    }

    suspend fun clearCache() {
        _otpNotes.update { emptyList() }
    }

    private fun loadOtpNotes(force: Boolean = false) = scope.launch {
        if (_otpNotes.value.isNotEmpty() && !force) return@launch
        if (consumers.get() <= 0) {
            // no consumers
            _otpNotes.value = emptyList()
            return@launch
        }

        if (_otpNotes.value.isEmpty()) {
            _otpNotes.value = engine().otpNotes()
                .map { it.coloredOtpNote(isLoaded = false) }
        }

        _otpNotes.value = engine().otpNotes(info = true)
            .map { it.coloredOtpNote(isLoaded = true) }
    }

}