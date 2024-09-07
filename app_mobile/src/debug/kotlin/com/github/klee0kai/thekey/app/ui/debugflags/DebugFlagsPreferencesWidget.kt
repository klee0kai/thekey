package com.github.klee0kai.thekey.app.ui.debugflags

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.ui.navigation.model.DebugFlagsDialogDestination
import com.github.klee0kai.thekey.core.R as CoreR
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.RightArrowIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced

@Composable
fun DebugFlagsPreferencesWidget(
    modifier: Modifier = Modifier,
) {
    val router = LocalRouter.current
    Preference(
        modifier = modifier,
        text = stringResource(id = CoreR.string.debug_flags),
        onClick = rememberClickDebounced { router.navigate(DebugFlagsDialogDestination) },
        icon = { RightArrowIcon() },
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun DebugFlagsPreferencesWidgetPreview() = DebugDarkContentPreview {
    DebugFlagsPreferencesWidget()
}