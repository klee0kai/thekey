package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter

import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.utils.common.launchIfNotStarted
import com.github.klee0kai.thekey.core.utils.coroutine.coldStateFlow
import com.github.klee0kai.thekey.core.utils.coroutine.touchable
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.perm.writeStoragePermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class FSStoragesPresenterImpl(
    private val origin: StoragesPresenter = object : StoragesPresenter {},
) : FSStoragesPresenter, StoragesPresenter by origin {

    private val scope = FSDI.defaultThreadScope()
    private val perm = FSDI.permissionsHelper()
    private val interactor = FSDI.findStoragesInteractorLazy()

    override val isStoragesSearchingProgress = MutableStateFlow(false)

    override val isPermissionGranted = coldStateFlow {
        result.value = perm.checkPermissions(perm.writeStoragePermissions())
    }.filterNotNull()
        .touchable()

    override fun requestPermissions(appRouter: AppRouter) = scope.launch {
        with(perm) {
            appRouter.askPermissionsIfNeed(
                perms = perm.writeStoragePermissions(),
                purpose = TextProvider(R.string.find_external_storage_purpose),
                skipDialogs = true,
            )
        }
        isPermissionGranted.touch()
    }

    override fun searchStorages() = scope.launchIfNotStarted("search") {
        isStoragesSearchingProgress.value = true
        interactor().findStoragesIfNeed(force = true)
        isStoragesSearchingProgress.value = false
    }

}

