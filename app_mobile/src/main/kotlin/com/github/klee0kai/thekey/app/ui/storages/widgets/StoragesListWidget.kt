package com.github.klee0kai.thekey.app.ui.storages.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.storages.components.InstallExternalSearchPromo
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.isInstalled
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetState
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha

@Composable
fun StoragesListWidget(
    modifier: Modifier = Modifier,
    state: StoragesListWidgetState = StoragesListWidgetState(),
) {
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val isFindStoragesNoteInstalled by presenter!!.installAutoSearchStatus.collectAsState(key = Unit, initial = null)

    val isShowInstallPluginPromo by animateTargetCrossFaded(
        target = isFindStoragesNoteInstalled?.let {
            state.isExtStorageSelected && !it.isInstalled
        },
        skipStates = listOf(null),
    )
    val showStoragesTitle by animateTargetCrossFaded(target = state.isShowStoragesTitle)

    when {
        isShowInstallPluginPromo.current == null -> Unit
        isShowInstallPluginPromo.current == true -> {
            InstallExternalSearchPromo(
                modifier = modifier
                    .alpha(isShowInstallPluginPromo.alpha),
            )
        }

        else -> {
            StoragesListContent(
                modifier = modifier
                    .fillMaxSize()
                    .alpha(isShowInstallPluginPromo.alpha),
                onEdit = { presenter?.editStorage(storagePath = it.path, router) },
                onExport = { presenter?.exportStorage(storagePath = it.path, router) },
                header = {
                    Text(
                        text = stringResource(id = R.string.storages),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                            .alpha(showStoragesTitle.visibleOnTargetAlpha(true))
                    )
                },
                footer = {
                    Spacer(modifier = modifier.height(safeContentPaddings.calculateBottomPadding()))
                },
            )
        }

    }

}


@Composable
@Preview
fun StoragesListWidgetPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    StoragesListWidget(
        state = StoragesListWidgetState(
            isExtStorageSelected = true,
        )
    )
}