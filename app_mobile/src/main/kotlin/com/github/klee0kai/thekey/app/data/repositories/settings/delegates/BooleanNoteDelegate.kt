package com.github.klee0kai.thekey.app.data.repositories.settings.delegates

import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class BooleanNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: Int,
    defaultValue: () -> Boolean,
) : SettingsNoteDelegate<Boolean>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it.toBoolean() },
    setTransform = { it.toString() }
)