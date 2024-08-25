package com.github.klee0kai.thekey.dynamic.findstorage.ui.settings.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.perm.writeStoragePermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

open class FSSettingsPresenterImpl : FSSettingsPresenter {

    private val scope = DI.defaultThreadScope()
    private val perm = FSDI.permissionsHelper()
    private val settings = FSDI.fsSettingsRepositoryLazy()

    private val _isAutoSearchEnable = MutableStateFlow<Boolean?>(null)
    override val isAutoSearchEnable = _isAutoSearchEnable.filterNotNull()

    override fun init() = scope.launch {
        _isAutoSearchEnable.value = settings().autoSearchEnabled()
    }

    override fun toggleAutoSearch(appRouter: AppRouter?) = scope.launch {
        val old = _isAutoSearchEnable.value ?: return@launch
        var newValue = !old
        if (newValue) {
            with(perm) {
                newValue = appRouter?.askPermissionsIfNeed(
                    perms = perm.writeStoragePermissions(),
                    purpose = TextProvider(R.string.find_external_storage_purpose),
                    skipDialogs = true,
                ) ?: false
            }
        }

        _isAutoSearchEnable.value = newValue
        settings().autoSearchEnabled.set(newValue)
    }

}