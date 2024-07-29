package com.github.klee0kai.thekey.core.data.repository.settings.delegates

import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class StringNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: String,
    defaultValue: () -> String,
) : SettingsNoteDelegate<String>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it },
    setTransform = { it }
)