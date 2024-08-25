package com.github.klee0kai.thekey.dynamic.findstorage.ui.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.StatusPreference
import com.github.klee0kai.thekey.core.ui.navigation.model.AutoSearchSettingsWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSPresentersModule
import com.github.klee0kai.thekey.dynamic.findstorage.ui.settings.presenter.FSSettingsPresenterDummy
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FSAutoSearchSettingsWidget(
    modifier: Modifier = Modifier,
    state: AutoSearchSettingsWidgetState = AutoSearchSettingsWidgetState,
) {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val resolver = LocalScreenResolver.current
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { FSDI.fsSettingsPresenter().apply { init() } }
    val enabled by presenter!!.isAutoSearchEnable.collectAsState(key = Unit, initial = null)

    StatusPreference(
        modifier = modifier,
        text = stringResource(id = R.string.storage_auto_search),
        status = when (enabled) {
            true -> stringResource(id = R.string.enabled)
            false -> stringResource(id = R.string.disabled)
            null -> ""
        },
        statusColor = when (enabled) {
            true -> theme.colorScheme.greenColor
            false -> theme.colorScheme.redColor
            null -> theme.colorScheme.hintTextColor
        },
        hint = when (enabled) {
            true -> stringResource(id = R.string.storage_auto_search_enabled_hint)
            false -> stringResource(id = R.string.storage_auto_search_disabled_hint)
            null -> ""
        },
        onClick = rememberClickDebounced(debounce = 100.milliseconds) {
            presenter?.toggleAutoSearch(router)
        },
    )
}


@Composable
@OptIn(DebugOnly::class)
@Preview
fun FSAutoSearchSettingsWidgetPreview() {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {
        override fun fsSettingsPresenter() = object : FSSettingsPresenterDummy() {

        }
    })
    DebugDarkContentPreview {
        FSAutoSearchSettingsWidget()
    }
}