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
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesButtonsWidgetId
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef

@Composable
fun StoragesButtonsWidget(
    widget: StoragesButtonsWidgetId = StoragesButtonsWidgetId()
) {
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val isExtStorageSelected by animateTargetCrossFaded(target = widget.isExtStorageSelected)
    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }

    if (isExtStorageSelected.current) {
        Column(
            modifier = Modifier
                .alpha(isExtStorageSelected.alpha)
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
                    Text(stringResource(textRes))
                }
            }

            if (!imeIsVisibleAnimated.current) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.installAutoSearchPlugin(router) }
                ) {
                    Text(stringResource(R.string.install))
                }
            }
        }
    } else {
        FabSimpleInContainer(
            modifier = Modifier.alpha(isExtStorageSelected.alpha),
            onClick = { router.navigate(EditStorageDestination()) },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }
}