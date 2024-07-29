package com.github.klee0kai.thekey.core.data.repository.settings.delegates

import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class IntNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: String,
    defaultValue: () -> Int,
) : SettingsNoteDelegate<Int>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it.toIntOrNull() ?: 0 },
    setTransform = { it.toString() }
)