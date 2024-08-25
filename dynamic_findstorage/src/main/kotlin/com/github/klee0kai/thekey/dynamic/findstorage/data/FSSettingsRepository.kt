package com.github.klee0kai.thekey.dynamic.findstorage.data

import com.github.klee0kai.thekey.core.data.repository.settings.SettingsRepository

class FSSettingsRepository : SettingsRepository() {

    val lastSearchTime = longDelegate(SETTING_LAST_SEARCH) { 0 }
    val autoSearchEnabled = booleanDelegate(SETTING_AUTO_SEARCH) { false }

    companion object {
        private const val SETTING_LAST_SEARCH = "fs_st"
        private const val SETTING_AUTO_SEARCH = "fs_auto_search"
    }

}