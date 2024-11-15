package com.github.klee0kai.thekey.app.ui.login.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.login.model.AppOffer
import com.github.klee0kai.thekey.app.ui.navigation.dest
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.SimpleDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.coldStateFlow
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import com.github.klee0kai.thekey.core.utils.coroutine.minDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

class LoginPresenterImpl(
    private val overrided: StorageIdentifier = StorageIdentifier(),
) : LoginPresenter {

    private val scope = DI.defaultThreadScope()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val loginInteractor = DI.loginInteractorLazy()

    override val currentStorageFlow = coldStateFlow<ColoredStorage> {
        val storagePath = overrided.path.takeIf { it.isNotBlank() }
            ?: settingsRep().currentStoragePath()
        val storage = storagesInteractor().findStorage(storagePath, mockNew = true).await()
        result.update { storage }
    }.filterNotNull()

    override val loginTrackFlow = MutableStateFlow<Int>(0)

    override val appOffer = lazyStateFlow(
        init = null as AppOffer?,
        defaultArg = Unit,
        scope = scope,
    ) {
        value = minDuration(1.seconds) {
            AppOffer.HowItWork
        }
    }

    override fun selectStorage(router: AppRouter?) = scope.launch {
        val selectedStorage = router
            ?.navigate<String>(StoragesDestination)
            ?.firstOrNull()

        if (selectedStorage != null) {
            settingsRep()
                .currentStoragePath
                .set(selectedStorage)
        }
    }

    override fun login(
        passw: String,
        router: AppRouter?,
    ) = scope.launch(trackFlow = loginTrackFlow) {
        if (passw.isBlank()) {
            router?.snack(R.string.passw_is_null)
            return@launch
        }
        router?.hideKeyboard()

        runCatching {
            val storageIdentifier = if (overrided.fileDescriptor != null) {
                overrided
            } else {
                currentStorageFlow.first().identifier()
            }

            loginInteractor()
                .login(storageIdentifier, passw)
                .await().let {
                    router?.navigate(it.dest())
                }
        }.onFailure { error ->
            Timber.d(error)
            router?.snack(error.message ?: "error")
        }
    }

    override fun appOfferClicked(
        router: AppRouter?,
    ) = scope.launch {
        when (appOffer.firstOrNull()) {
            AppOffer.EnableAnalytics -> {
                router?.navigate(
                    SimpleDialogDestination(
                        title = TextProvider(R.string.enable_app_analytics),
                        message = TextProvider(R.string.enable_app_analytics_message),
                        confirm = TextProvider(R.string.confirm),
                        reject = TextProvider(R.string.reject),
                    )
                )
            }

            AppOffer.EnableAutoFill -> {
                router?.navigate(
                    SimpleDialogDestination(
                        title = TextProvider(R.string.enable_password_autofill),
                        message = TextProvider(R.string.enable_password_autofill_message),
                        confirm = TextProvider(R.string.confirm),
                        reject = TextProvider(R.string.cancel),
                    )
                )
            }

            AppOffer.EnableAutoSearch -> {

            }

            AppOffer.EnableBackup -> {

            }

            AppOffer.HowItWork -> {
                router?.navigate(
                    SimpleDialogDestination(
                        title = TextProvider(R.string.how_it_work),
                        message = TextProvider(R.string.how_it_work_message),
                        confirm = TextProvider(R.string.ok),
                    )
                )
            }

            is AppOffer.Promo -> {

            }

            AppOffer.RateApp -> {

            }

            null -> {

            }
        }
    }
}
