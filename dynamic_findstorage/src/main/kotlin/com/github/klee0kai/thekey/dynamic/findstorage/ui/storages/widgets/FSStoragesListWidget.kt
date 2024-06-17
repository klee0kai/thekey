package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesListWidgetState
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.content.RequestExternalStoragePermissionsContent

@Composable
fun FSStoragesListWidget(
    modifier: Modifier = Modifier,
    state: StoragesListWidgetState = StoragesListWidgetState(),
    parent: @Composable (modifier: Modifier, state: StoragesListWidgetState) -> Unit = { _, _ -> },
) {
    val presenter by rememberOnScreenRef { FSDI.fsStoragesPresenter() }
    val isPermissionsGranted by presenter!!.isPermissionGranted.collectAsState(key = Unit, initial = null)

    val showPermissionAnimated by animateTargetCrossFaded(
        target = state.isExtStorageSelected && isPermissionsGranted == false
    )

    when (showPermissionAnimated.current) {
        true -> {
            RequestExternalStoragePermissionsContent()
        }

        false -> {
            parent.invoke(
                modifier = modifier,
                state = state
            )
        }
    }
}


@Composable
@Preview
fun FSStoragesListWidgetPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FSStoragesListWidget(
        state = StoragesListWidgetState(
            isExtStorageSelected = true,
        )
    )
}