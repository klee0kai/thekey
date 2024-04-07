package com.github.klee0kai.thekey.app.data.repositories.settings.delegates

import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class IntNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: Int,
    defaultValue: () -> Int,
) : SettingsNoteDelegate<Int>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it.toIntOrNull() ?: 0 },
    setTransform = { it.toString() }
)