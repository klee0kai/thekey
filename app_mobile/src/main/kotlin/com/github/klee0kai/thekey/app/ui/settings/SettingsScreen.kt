package com.github.klee0kai.thekey.app.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.AboutDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.AutoFillSettingsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.BackupSettings
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.SubscriptionsDestination
import com.github.klee0kai.thekey.app.ui.settings.presenter.SettingsPresenterDummy
import com.github.klee0kai.thekey.core.BuildConfig
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import com.github.klee0kai.thekey.core.domain.model.nextRecursive
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.RightArrowIcon
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SectionHeader
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.StatusPreference
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.navigation.model.AutoSearchSettingsWidgetState
import com.github.klee0kai.thekey.core.ui.navigation.model.DebugSettingsWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SettingScreen() = Screen {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val resolver = LocalScreenResolver.current
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.settingsPresenter().apply { init() } }
    val state by presenter!!.state.collectAsState(key = Unit, initial = null)
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item("top_padding") {
            Spacer(
                modifier = Modifier.height(safeContentPaddings.calculateTopPadding() + AppBarConst.appBarSize)
            )
        }

        item("common_header") {
            SectionHeader(
                text = stringResource(id = R.string.common),
            )
        }

        item("subscriptions") {
            Preference(
                text = stringResource(id = R.string.subscriptions),
                onClick = rememberClickDebounced { router.navigate(SubscriptionsDestination()) },
                icon = { RightArrowIcon() },
            )
        }

        item("storages_header") {
            SectionHeader(
                text = stringResource(id = R.string.storages),
            )
        }


        if (state?.loginSecure != null) item("login_secure") {
            StatusPreference(
                text = stringResource(id = R.string.secure),
                status = when (state?.loginSecure) {
                    LoginSecureMode.LOW_SECURE -> stringResource(id = R.string.low)
                    LoginSecureMode.MIDDLE_SECURE -> stringResource(id = R.string.middle)
                    LoginSecureMode.HARD_SECURE -> stringResource(id = R.string.strong)
                    null -> ""
                },
                statusColor = when (state?.loginSecure) {
                    LoginSecureMode.LOW_SECURE -> theme.colorScheme.redColor
                    LoginSecureMode.MIDDLE_SECURE -> theme.colorScheme.yellowColor
                    LoginSecureMode.HARD_SECURE -> theme.colorScheme.greenColor
                    null -> theme.colorScheme.hintTextColor
                },
                hint = when (state?.loginSecure) {
                    LoginSecureMode.LOW_SECURE -> stringResource(id = R.string.secure_low_hint)
                    LoginSecureMode.MIDDLE_SECURE -> stringResource(id = R.string.secure_middle_hint)
                    LoginSecureMode.HARD_SECURE -> stringResource(id = R.string.secure_strong_hint)
                    null -> ""
                },
                onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                    presenter?.input { copy(loginSecure = loginSecure?.nextRecursive()) }
                },
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            item("autofill") {
                Preference(
                    text = stringResource(id = R.string.title_autofill),
                    hint = stringResource(id = R.string.title_autofill_hint),
                    onClick = rememberClickDebounced { router.navigate(AutoFillSettingsDestination) },
                    icon = { RightArrowIcon() },
                )
            }
        }

        item("backup") {
            Preference(
                text = stringResource(id = R.string.backup),
                hint = stringResource(id = R.string.backup_hint),
                onClick = rememberClickDebounced { router.navigate(BackupSettings) },
                icon = { RightArrowIcon() },
            )
        }

        if (state?.histPeriod != null) item("hist_period") {
            StatusPreference(
                text = stringResource(id = R.string.hist_period),
                status = when (state?.histPeriod) {
                    HistPeriod.SHORT -> stringResource(id = R.string.hist_period_short)
                    HistPeriod.NORMAL -> stringResource(id = R.string.hist_period_normal)
                    HistPeriod.LONG -> stringResource(id = R.string.hist_period_long)
                    HistPeriod.VERY_LONG -> stringResource(id = R.string.hist_period_very_long)
                    null -> ""
                },
                statusColor = theme.colorScheme.hintTextColor,
                hint = when (state?.histPeriod) {
                    HistPeriod.SHORT -> stringResource(id = R.string.hist_period_short_hint)
                    HistPeriod.NORMAL -> stringResource(id = R.string.hist_period_normal_hint)
                    HistPeriod.LONG -> stringResource(id = R.string.hist_period_long_hint)
                    HistPeriod.VERY_LONG -> stringResource(id = R.string.hist_period_verylong_hint)
                    null -> ""

                },
                onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                    presenter?.input { copy(histPeriod = histPeriod?.nextRecursive()) }
                },
            )
        }

        item("new_storages_header") {
            SectionHeader(
                text = stringResource(id = R.string.new_storages),
            )
        }

        item("auto_search") {
            resolver.widget(modifier = Modifier, widgetState = AutoSearchSettingsWidgetState)
        }

        if (state?.encryptionComplexity != null) item("encryption_complexity") {
            StatusPreference(
                text = stringResource(id = R.string.encryption_complexity),
                status = when (state?.encryptionComplexity) {
                    NewStorageSecureMode.LOW_SECURE -> stringResource(id = R.string.low)
                    NewStorageSecureMode.MIDDLE_SECURE -> stringResource(id = R.string.middle)
                    NewStorageSecureMode.HARD_SECURE -> stringResource(id = R.string.strong)
                    null -> ""
                },
                statusColor = when (state?.encryptionComplexity) {
                    NewStorageSecureMode.LOW_SECURE -> theme.colorScheme.redColor
                    NewStorageSecureMode.MIDDLE_SECURE -> theme.colorScheme.yellowColor
                    NewStorageSecureMode.HARD_SECURE -> theme.colorScheme.greenColor
                    null -> theme.colorScheme.hintTextColor
                },
                hint = when (state?.encryptionComplexity) {
                    NewStorageSecureMode.LOW_SECURE -> stringResource(id = R.string.encryption_low_hint)
                    NewStorageSecureMode.MIDDLE_SECURE -> stringResource(id = R.string.encryption_middle_hint)
                    NewStorageSecureMode.HARD_SECURE -> stringResource(id = R.string.encryption_strong_hint)
                    null -> ""
                },
                onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                    presenter?.input { copy(encryptionComplexity = encryptionComplexity?.nextRecursive()) }
                },
            )
        }

        item("other_header") {
            SectionHeader(
                text = stringResource(id = R.string.other)
            )
        }

        item("plugins") {
            Preference(
                text = stringResource(id = R.string.plugins),
                hint = stringResource(id = R.string.plugins_hint),
                onClick = rememberClickDebounced { router.navigate(PluginsDestination) },
                icon = { RightArrowIcon() },
            )
        }

        if (state != null) item("analytics") {
            StatusPreference(
                text = stringResource(id = R.string.analytics),
                status = when (state?.analytics) {
                    true -> stringResource(id = R.string.enabled)
                    false -> stringResource(id = R.string.disabled)
                    null -> ""
                },
                statusColor = when (state?.analytics) {
                    true -> theme.colorScheme.greenColor
                    false -> theme.colorScheme.redColor
                    null -> theme.colorScheme.hintTextColor
                },
                hint = when (state?.analytics) {
                    true -> stringResource(id = R.string.analytics_enabled_hint)
                    false -> stringResource(id = R.string.analytics_disabled_hint)
                    null -> ""
                },
                onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                    presenter?.input { copy(analytics = !(state?.analytics ?: false)) }
                },
            )
        }

        item("about") {
            Preference(
                text = stringResource(id = R.string.about),
                onClick = rememberClickDebounced { router.navigate(AboutDestination) },
                icon = { RightArrowIcon() },
            )
        }

        if (BuildConfig.DEBUG) {
            item("debug_header") {
                SectionHeader(
                    text = stringResource(id = R.string.debug),
                )

                Column {
                    resolver.widget(modifier = Modifier, widgetState = DebugSettingsWidgetState)
                }
            }
        }


        item("bottom_padding") {
            Spacer(modifier = Modifier.height(safeContentPaddings.calculateBottomPadding()))
        }

    }

    AppBarStates(
        isVisible = !scrollState.canScrollBackward,
        navigationIcon = {
            IconButton(onClick = rememberClickDebounced { router.back() }) { BackMenuIcon() }
        },
        titleContent = {
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.settings)
            )
        }
    )
}


@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun SettingsScreenPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun settingsPresenter() = object : SettingsPresenterDummy() {

        }
    })
    DebugDarkScreenPreview {
        SettingScreen()
    }
}