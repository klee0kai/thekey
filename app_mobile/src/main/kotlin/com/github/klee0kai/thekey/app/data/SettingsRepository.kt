package com.github.klee0kai.thekey.app.data

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsRepository {

    private val settingsDao by DI.settingDaoLazy()

    private val scope = DI.ioThreadScope()

    fun get(settingId: Int) = scope.async {
        settingsDao[settingId]?.value
    }

    fun set(settingId: Int, value: String) = scope.launch {
        val entry = SettingPairEntry(id = settingId, value = value)
        settingsDao.update(entry = entry)
    }

    companion object {
        const val SETTING_DEFAULT_STORAGE_PATH = 944
        const val SETTING_GEN_PASS_LEN = 247
        const val SETTING_GEN_PASS_INCLUDE_EN = 43
        const val SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = 44
    }

}