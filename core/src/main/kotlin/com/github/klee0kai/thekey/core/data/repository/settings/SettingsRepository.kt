package com.github.klee0kai.thekey.core.data.repository.settings

import com.github.klee0kai.thekey.core.data.repository.settings.delegates.BooleanNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.IntNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.LongNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.SettingsNoteDelegate
import com.github.klee0kai.thekey.core.data.repository.settings.delegates.StringNoteDelegate
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import com.github.klee0kai.thekey.core.utils.error.fatalError
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

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
    val loginSecure = delegate<LoginSecureMode>(SETTING_LOGIN_SECURE) {
        LoginSecureMode.LOW_SECURE
    }
    val encryptionComplexity = delegate<NewStorageSecureMode>(SETTING_ENCR_SEC) {
        NewStorageSecureMode.LOW_SECURE
    }
    val histPeriod = delegate<HistPeriod>(SETTING_HISTORY_PERIOD) { HistPeriod.NORMAL }
    val lastCleanHistTime = longDelegate(SETTING_LAST_HIST_CLEAN) { 0L }
    val analytics = booleanDelegate(SETTING_ANALYTICS) { false }


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
        private const val SETTING_LOGIN_SECURE = "base_login_secure"
        private const val SETTING_ENCR_SEC = "base_encr_sec"
        private const val SETTING_HISTORY_PERIOD = "base_hist_period"
        private const val SETTING_LAST_HIST_CLEAN = "base_lst_hst_cln"
        private const val SETTING_ANALYTICS = "base_analytics"
    }

}