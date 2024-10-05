package com.github.klee0kai.thekey.app.ui.storages.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.NotInstalled
import com.github.klee0kai.thekey.core.domain.model.feature.model.isInstalled
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun StoragesButtonsWidget(
    modifier: Modifier = Modifier,
    state: StoragesButtonsWidgetState = StoragesButtonsWidgetState(),
) {
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val isFindStoragesFeatureInstalled by presenter!!.installAutoSearchStatus.collectAsState(
        key = Unit,
        initial = null
    )

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)

    val isShowInstallPluginPromo by animateTargetCrossFaded(
        target = isFindStoragesFeatureInstalled?.let {
            state.isExtStorageSelected && !it.isInstalled
        },
        skipStates = listOf(null),
    )

    when {
        isShowInstallPluginPromo.current == null -> Unit
        isShowInstallPluginPromo.current == true -> {
            Column(
                modifier = modifier
                    .alpha(isShowInstallPluginPromo.alpha)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeContent)
                    .padding(
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                if (!imeIsVisibleAnimated.current) {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = LocalColorScheme.current.grayTextButtonColors,
                        onClick = { presenter?.importStorage(router) }
                    ) {
                        val textRes = R.string.import_storage
                        Text(
                            text = stringResource(textRes),
                            style = theme.typeScheme.buttonText,
                        )
                    }
                }

                if (!imeIsVisibleAnimated.current) {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(imeIsVisibleAnimated.alpha),
                        onClick = { presenter?.installAutoSearchPlugin(router) }
                    ) {
                        Text(
                            text = stringResource(R.string.install),
                            style = theme.typeScheme.buttonText,
                        )
                    }
                }
            }
        }

        else -> {
            FabSimpleInContainer(
                modifier = modifier
                    .alpha(isShowInstallPluginPromo.alpha),
                onClick = rememberClickDebounced { router?.navigate(EditStorageDestination()) },
                content = { Icon(Icons.Default.Add, contentDescription = "Add") }
            )
        }
    }
}

@DebugOnly
@Composable
@Preview
fun StoragesButtonsWidgetPromoPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagesPresenter() = object : StoragesPresenter {
            override val installAutoSearchStatus = MutableStateFlow(NotInstalled)
        }
    })
    StoragesButtonsWidget(
        state = StoragesButtonsWidgetState(
            isExtStorageSelected = true,
        )
    )
}


@DebugOnly
@Composable
@Preview
fun StoragesButtonsWidgetDefaultPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.hardResetToPreview()
    StoragesButtonsWidget(
        state = StoragesButtonsWidgetState(
            isExtStorageSelected = true,
        )
    )
}