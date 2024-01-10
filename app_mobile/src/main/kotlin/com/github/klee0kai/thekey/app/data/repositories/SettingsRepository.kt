package com.github.klee0kai.thekey.app.data.repositories

import com.github.klee0kai.thekey.app.data.repositories.delegates.SettingsNoteDelegate
import com.github.klee0kai.thekey.app.di.DI
import java.io.File

class SettingsRepository {

    private val settingsDao = DI.settingDaoLazy()
    private val scope = DI.ioThreadScope()

    val currentStoragePath = noteDelegate(SETTING_DEFAULT_STORAGE_PATH) {
        File(DI.app().applicationInfo?.dataDir, "keys.ckey").path
    }


    private fun noteDelegate(
        settingId: Int,
        defaultValue: () -> String
    ) = SettingsNoteDelegate(settingsDao, scope, settingId, defaultValue)

    companion object {
        private const val SETTING_DEFAULT_STORAGE_PATH = 944
        private const val SETTING_GEN_PASS_LEN = 247
        private const val SETTING_GEN_PASS_INCLUDE_EN = 43
        private const val SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = 44
    }

}