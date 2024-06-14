package com.github.klee0kai.thekey.core.data.repository.settings.delegates

import com.github.klee0kai.thekey.core.data.room.dao.SettingDao
import com.github.klee0kai.thekey.core.data.room.entry.SettingPairEntry
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class SettingsNoteDelegate<T>(
    private val settingsDao: AsyncCoroutineProvide<SettingDao>,
    private val scope: CoroutineScope,
    private val settingId: Int,
    private val defaultValue: () -> T,
    private val getTransform: (String) -> T,
    private val setTransform: (T) -> String,
) {

    fun set(value: T): Job = scope.launch {
        val entry = SettingPairEntry(id = settingId, value = setTransform(value))
        settingsDao().update(entry = entry)
    }

    fun get(): Deferred<T> = scope.async {
        settingsDao()[settingId]
            ?.value
            ?.let { getTransform(it) }
            ?: defaultValue()
    }

    suspend operator fun invoke() = get().await()

}