package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.externalStorages
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSPresentersModule
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter.FSStoragesPresenter
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun FSStoragesButtonsWidget(
    modifier: Modifier = Modifier,
    widget: StoragesButtonsWidgetState = StoragesButtonsWidgetState(),
    parent: @Composable (modifier: Modifier, state: StoragesListWidgetState) -> Unit = { _, _ -> },
) = Box(modifier = modifier.fillMaxSize()) {
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { FSDI.fsStoragesPresenter() }
    val extGroup by presenter!!.externalStoragesColorGroup.collectAsState(
        key = Unit,
        initial = ColorGroup.externalStorages()
    )

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isPermissionsGranted by presenter!!.isPermissionGranted.collectAsState(
        key = Unit,
        initial = null
    )
    val isStorageSearching by presenter!!.isStoragesSearchingProgress.collectAsState(
        key = Unit,
        initial = false
    )

    val isExtStorageSelected by animateTargetCrossFaded(
        target = widget.isExtStorageSelected,
    )
    val showPermissionAnimated by animateTargetCrossFaded(
        target = isPermissionsGranted?.let { widget.isExtStorageSelected && !it },
        skipStates = listOf(null),
    )
    val showSearchExt = isExtStorageSelected.current && showPermissionAnimated.current == false
    val hideSearchWhileSearching by animateTargetCrossFaded(target = showSearchExt && isStorageSearching)

    when {
        showPermissionAnimated.current == null -> Unit
        imeIsVisibleAnimated.current -> Unit
        showPermissionAnimated.current == true -> {
            Column(
                modifier = Modifier
                    .alpha(showPermissionAnimated.alpha)
                    .alpha(imeIsVisibleAnimated.alpha)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeContent)
                    .padding(
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    colors = LocalColorScheme.current.grayTextButtonColors,
                    onClick = { presenter?.importStorage(router) }
                ) {
                    val textRes = R.string.import_storage
                    Text(
                        text = stringResource(textRes),
                        style = theme.typeScheme.buttonText,
                    )
                }

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.requestPermissions(router) }
                ) {
                    Text(
                        text = stringResource(R.string.grant_permissions),
                        style = theme.typeScheme.buttonText,
                    )
                }
            }
        }

        hideSearchWhileSearching.current -> {
            // no button. Searching already started
        }

        else -> {
            FabSimpleInContainer(
                modifier = Modifier
                    .alpha(showPermissionAnimated.alpha)
                    .alpha(hideSearchWhileSearching.alpha)
                    .alpha(imeIsVisibleAnimated.alpha),
                square = showSearchExt,
                containerColor = if (showSearchExt) {
                    theme.colorScheme.surfaceSchemas.surfaceScheme(extGroup.keyColor).surfaceColor
                } else {
                    theme.colorScheme.androidColorScheme.secondaryContainer
                },
                onClick = rememberClickDebounced(showSearchExt) {
                    if (showSearchExt) {
                        presenter?.searchStorages()
                    } else {
                        router?.navigate(EditStorageDestination())
                    }
                },
                content = {
                    if (showSearchExt) {
                        Icon(
                            Icons.Default.Search,
                            modifier = Modifier.alpha(isExtStorageSelected.alpha),
                            contentDescription = "Search",
                        )
                    } else {
                        Icon(
                            Icons.Default.Add,
                            modifier = Modifier.alpha(isExtStorageSelected.alpha),
                            contentDescription = "Add"
                        )
                    }
                }
            )
        }
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun FSStoragesButtonsNotGrantedPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {
        override fun fsStoragesPresenter(origin: StoragesPresenter) = object : FSStoragesPresenter {
            override val isPermissionGranted = MutableStateFlow(false)
        }
    })
    FSStoragesButtonsWidget(
        widget = StoragesButtonsWidgetState(
            isExtStorageSelected = true,
        )
    )
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun FSStoragesButtonsGrantedPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {
        override fun fsStoragesPresenter(origin: StoragesPresenter) = object : FSStoragesPresenter {
            override val isPermissionGranted = MutableStateFlow(true)
        }
    })
    FSStoragesButtonsWidget(
        widget = StoragesButtonsWidgetState(
            isExtStorageSelected = true,
        )
    )
}