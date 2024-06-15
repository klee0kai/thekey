package com.github.klee0kai.thekey.core.data.repository.settings

import com.github.klee0kai.thekey.core.data.repository.settings.delegates.BooleanNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.IntNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.StringNoteDelegate
import com.github.klee0kai.thekey.core.di.CoreDI
import java.io.File

open class SettingsRepository {

    private val settingsDao = CoreDI.settingDaoLazy()
    private val scope = CoreDI.ioThreadScope()

    val currentStoragePath = stringDelegate(SETTING_DEFAULT_STORAGE_PATH) {
        File(CoreDI.ctx().applicationInfo?.dataDir, "keys.ckey").path
    }

    val newStorageVersion = intDelegate(SETTING_DEFAULT_STORAGE_VERSION) { 2 }

    val genPasswLen = intDelegate(SETTING_GEN_PASS_LEN) { 4 }
    val genPasswIncludeSymbols = booleanDelegate(SETTING_GEN_PASS_INCLUDE_EN) { false }
    val genPasswIncludeSpecSymbols = booleanDelegate(SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS) { false }

    val externalStoragesGroup = booleanDelegate(SETTING_EXTERNAL_STORAGES_GROUP) { true }
    val otpNotesGroup = booleanDelegate(SETTING_OTP_NOTES_GROUP) { true }


    protected fun stringDelegate(
        settingId: Int,
        defaultValue: () -> String
    ) = StringNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected fun intDelegate(
        settingId: Int,
        defaultValue: () -> Int
    ) = IntNoteDelegate(settingsDao, scope, settingId, defaultValue)

    protected fun booleanDelegate(
        settingId: Int,
        defaultValue: () -> Boolean
    ) = BooleanNoteDelegate(settingsDao, scope, settingId, defaultValue)


    // check with AutoFillSettingsRepository
    companion object {
        private const val SETTING_DEFAULT_STORAGE_PATH = 944
        private const val SETTING_DEFAULT_STORAGE_VERSION = 432
        private const val SETTING_GEN_PASS_LEN = 247
        private const val SETTING_GEN_PASS_INCLUDE_EN = 43
        private const val SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = 44
        private const val SETTING_EXTERNAL_STORAGES_GROUP = 55
        private const val SETTING_OTP_NOTES_GROUP = 55
    }

}