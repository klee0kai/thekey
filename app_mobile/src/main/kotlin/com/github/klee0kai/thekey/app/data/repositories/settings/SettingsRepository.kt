package com.github.klee0kai.thekey.app.data.repositories.settings

import com.github.klee0kai.thekey.app.data.repositories.settings.delegates.BooleanNoteDelegate
import com.github.klee0kai.thekey.app.data.repositories.settings.delegates.IntNoteDelegate
import com.github.klee0kai.thekey.app.data.repositories.settings.delegates.StringNoteDelegate
import com.github.klee0kai.thekey.app.di.DI
import java.io.File

class SettingsRepository {

    private val settingsDao = DI.settingDaoLazy()
    private val scope = DI.ioThreadScope()

    val currentStoragePath = stringDelegate(SETTING_DEFAULT_STORAGE_PATH) {
        File(DI.app().applicationInfo?.dataDir, "keys.ckey").path
    }

    val newStorageVersion = intDelegate(SETTING_DEFAULT_STORAGE_VERSION) { 2 }

    val genPasswLen = intDelegate(SETTING_GEN_PASS_LEN) { 4 }
    val genPasswIncludeSymbols = booleanDelegate(SETTING_GEN_PASS_INCLUDE_EN) { false }
    val genPasswIncludeSpecSymbols = booleanDelegate(SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS) { false }


    private fun stringDelegate(
        settingId: Int,
        defaultValue: () -> String
    ) = StringNoteDelegate(settingsDao, scope, settingId, defaultValue)

    private fun intDelegate(
        settingId: Int,
        defaultValue: () -> Int
    ) = IntNoteDelegate(settingsDao, scope, settingId, defaultValue)

    private fun booleanDelegate(
        settingId: Int,
        defaultValue: () -> Boolean
    ) = BooleanNoteDelegate(settingsDao, scope, settingId, defaultValue)

    companion object {
        private const val SETTING_DEFAULT_STORAGE_PATH = 944
        private const val SETTING_DEFAULT_STORAGE_VERSION = 432
        private const val SETTING_GEN_PASS_LEN = 247
        private const val SETTING_GEN_PASS_INCLUDE_EN = 43
        private const val SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = 44
    }

}