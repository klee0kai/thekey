package com.github.klee0kai.thekey.core.data.repository.settings

import com.github.klee0kai.thekey.core.data.repository.settings.delegates.BooleanNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.IntNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.LongNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.SettingsNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.StringNoteDelegate
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.utils.error.fatalError
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

open class SettingsRepository {

    val settingsDao = CoreDI.settingDaoLazy()
    val scope = CoreDI.ioThreadScope()
    val userShortPaths = CoreDI.userShortPaths()
    val jsonEngine = Json

    val currentStoragePath = stringDelegate(SETTING_DEFAULT_STORAGE_PATH) {
        File(userShortPaths.appPath, "keys.ckey").path
    }

    val newStorageVersion = intDelegate(SETTING_DEFAULT_STORAGE_VERSION) { 2 }

    val genPasswLen = intDelegate(SETTING_GEN_PASS_LEN) { 4 }
    val genPasswIncludeSymbols = booleanDelegate(SETTING_GEN_PASS_INCLUDE_EN) { false }
    val genPasswIncludeSpecSymbols =
        booleanDelegate(SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS) { false }

    val externalStoragesGroup = booleanDelegate(SETTING_EXTERNAL_STORAGES_GROUP) { true }
    val otpNotesGroup = booleanDelegate(SETTING_OTP_NOTES_GROUP) { true }
    val logoutTimeout = delegate<Duration>(SETTING_LOGOUT_TIMEOUT) { 1.minutes }


    protected fun stringDelegate(
        settingId: String,
        defaultValue: () -> String
    ) = StringNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected fun intDelegate(
        settingId: String,
        defaultValue: () -> Int
    ) = IntNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected fun longDelegate(
        settingId: String,
        defaultValue: () -> Long
    ) = LongNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected fun booleanDelegate(
        settingId: String,
        defaultValue: () -> Boolean
    ) = BooleanNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected inline fun <reified T> delegate(
        settingId: String,
        noinline defaultValue: () -> T,
    ) = SettingsNoteDelegate<T>(
        settingsDao, scope, settingId, defaultValue,
        getTransform = {
            runCatching<T> {
                jsonEngine.decodeFromString(it)
            }.fatalError()
                .getOrNull()
                ?: defaultValue()
        },
        setTransform = {
            runCatching { jsonEngine.encodeToString(it) }
                .fatalError()
                .getOrNull() ?: ""
        }
    )


    companion object {
        private const val SETTING_DEFAULT_STORAGE_PATH = "base_st_path"
        private const val SETTING_DEFAULT_STORAGE_VERSION = "base_st_ver"
        private const val SETTING_GEN_PASS_LEN = "base_gen_len"
        private const val SETTING_GEN_PASS_INCLUDE_EN = "base_en"
        private const val SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = "base_spec"
        private const val SETTING_EXTERNAL_STORAGES_GROUP = "base_gr"
        private const val SETTING_OTP_NOTES_GROUP = "base_otp_gr"
        private const val SETTING_LOGOUT_TIMEOUT = "base_logout_timeout"
    }

}