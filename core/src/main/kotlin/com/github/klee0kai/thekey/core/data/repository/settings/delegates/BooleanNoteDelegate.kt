package com.github.klee0kai.thekey.core.data.repository.settings.delegates

import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class BooleanNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: String,
    defaultValue: () -> Boolean,
) : SettingsNoteDelegate<Boolean>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it.toBoolean() },
    setTransform = { it.toString() }
)