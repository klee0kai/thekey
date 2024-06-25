package com.github.klee0kai.thekey.dynamic.findstorage.data

import com.github.klee0kai.thekey.core.data.repository.settings.SettingsRepository

class FSSettingsRepository : SettingsRepository() {


    val lastSearchTime = longDelegate(SETTING_LAST_SEARCH) { 0 }

    /**
     * check with
     * [SettingsRepository] / [AutoFillSettingsRepository] / [FSSettingsRepository]
     */
    companion object {
        private const val SETTING_LAST_SEARCH = 378
    }

}