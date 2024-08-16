package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter

import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.utils.common.launchDebounced
import com.github.klee0kai.thekey.core.utils.coroutine.coldStateFlow
import com.github.klee0kai.thekey.core.utils.coroutine.touchable
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.perm.writeStoragePermissions
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

class FSStoragesPresenterImpl(
    private val origin: StoragesPresenter = object : StoragesPresenter {},
) : FSStoragesPresenter, StoragesPresenter by origin {

    private val scope = FSDI.defaultThreadScope()
    private val perm = FSDI.permissionsHelper()
    private val interactor = FSDI.findStoragesInteractorLazy()
    private val storagesInteractor = FSDI.storagesInteractorLazy()

    override val externalStoragesColorGroup = flow {
        storagesInteractor().externalStoragesGroup.collect(this)
    }

    override val isStoragesSearchingProgress = flow { interactor().searchState.collect(this) }

    override val isPermissionGranted = coldStateFlow {
        result.value = perm.checkPermissions(perm.writeStoragePermissions())
    }.filterNotNull()
        .touchable()

    override fun requestPermissions(appRouter: AppRouter?) = scope.launchDebounced("req_perm") {
        with(perm) {
            appRouter?.askPermissionsIfNeed(
                perms = perm.writeStoragePermissions(),
                purpose = TextProvider(R.string.find_external_storage_purpose),
                skipDialogs = true,
            )
        }
        isPermissionGranted.touch()
    }

    override fun searchStorages() = scope.launchDebounced("search") {
        interactor().findStoragesIfNeed(force = true)
    }

}

