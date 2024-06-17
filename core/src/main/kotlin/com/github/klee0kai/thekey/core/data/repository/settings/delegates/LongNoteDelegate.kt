package com.github.klee0kai.thekey.core.data.repository.settings.delegates

import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope

class LongNoteDelegate(
    settingsDao: AsyncCoroutineProvide<SettingDao>,
    scope: CoroutineScope,
    settingId: Int,
    defaultValue: () -> Long,
) : SettingsNoteDelegate<Long>(
    settingsDao, scope, settingId, defaultValue,
    getTransform = { it.toLongOrNull() ?: 0L },
    setTransform = { it.toString() }
)