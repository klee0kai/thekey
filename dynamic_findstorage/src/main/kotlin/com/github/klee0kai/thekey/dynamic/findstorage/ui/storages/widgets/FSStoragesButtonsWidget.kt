package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetId
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview

@Composable
fun FSStoragesButtonsWidget(
    widget: StoragesButtonsWidgetId = StoragesButtonsWidgetId()
) {
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { FSDI.fsStoragesPresenter() }

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isPermissionsGranted by presenter!!.isPermissionGranted.collectAsState(key = Unit, initial = null)
    val isExtStorageSelected by animateTargetCrossFaded(target = widget.isExtStorageSelected)
    val showPermissionAnimated by animateTargetCrossFaded(
        target = widget.isExtStorageSelected && isPermissionsGranted == false
    )

    when {
        imeIsVisibleAnimated.current -> Unit
        showPermissionAnimated.current -> {
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
                        .fillMaxWidth(),
                    colors = LocalColorScheme.current.grayTextButtonColors,
                    onClick = { presenter?.importStorage(router) }
                ) {
                    val textRes = R.string.import_storage
                    Text(stringResource(textRes))
                }

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.requestPermissions(router) }
                ) {
                    Text(stringResource(R.string.grant_permissions))
                }
            }
        }

        else -> {
            FabSimpleInContainer(
                modifier = Modifier
                    .alpha(showPermissionAnimated.alpha)
                    .alpha(imeIsVisibleAnimated.alpha),
                square = widget.isExtStorageSelected,
                containerColor = if (widget.isExtStorageSelected) {
                    theme.colorScheme.androidColorScheme.primaryContainer
                } else {
                    theme.colorScheme.androidColorScheme.secondaryContainer
                },
                onClick = {
                    if (isExtStorageSelected.current) {
                        presenter?.importStorage(router)
                    } else {
                        router.navigate(EditStorageDestination())
                    }
                },
                content = {
                    if (isExtStorageSelected.current) {
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
fun FSStoragesButtonsWidgetPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FSDI.hardResetToPreview()
    FSStoragesButtonsWidget(
        widget = StoragesButtonsWidgetId(
            isExtStorageSelected = true,
        )
    )
}