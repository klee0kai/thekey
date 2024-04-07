package com.github.klee0kai.thekey.app.data.repositories.settings.delegates

import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class StringNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: Int,
    defaultValue: () -> String,
) : SettingsNoteDelegate<String>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it },
    setTransform = { it }
)