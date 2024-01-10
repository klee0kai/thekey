package com.github.klee0kai.thekey.app.data.repositories.delegates

import com.github.klee0kai.thekey.app.data.room.dao.SettingDao
import com.github.klee0kai.thekey.app.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsNoteDelegate(
    private val settingsDao: AsyncCoroutineProvide<SettingDao>,
    private val scope: CoroutineScope,
    private val settingId: Int,
    private val defaultValue: () -> String,
) {

    fun set(value: String): Job = scope.launch {
        val entry = SettingPairEntry(id = settingId, value = value)
        settingsDao().update(entry = entry)
    }

    fun get(): Deferred<String> = scope.async {
        settingsDao()[settingId]
            ?.value
            ?: defaultValue()
    }

}