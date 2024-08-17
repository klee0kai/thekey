package com.github.klee0kai.thekey.app.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.github.klee0kai.thekey.app.ui.navigation.model.AboutDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.AutoFillSettingsDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.BackupSettings
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination
import com.github.klee0kai.thekey.core.BuildConfig
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.RightArrowIcon
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SectionHeader
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreference
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackIcon
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.DebugSettingsWidgetState
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.truncate
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate

@Composable
fun SettingScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val resolver = LocalScreenResolver.current


    LazyColumn(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize(),
        contentPadding = WindowInsets.safeContent
            .truncate(right = true, left = true)
            .asPaddingValues(),
    ) {
        item {
            SectionHeader(
                text = stringResource(id = R.string.storages)
            )
        }

        item {
            SwitchPreference(
                text = stringResource(id = R.string.storage_auto_search)
            )
        }

        item {
            Preference(
                text = stringResource(id = R.string.backup),
                onClick = rememberClickDebounced {
                    router.navigate(BackupSettings)
                },
                icon = { RightArrowIcon() }
            )
        }

        item {
            SectionHeader(
                text = stringResource(id = R.string.other)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            item {
                Preference(
                    text = stringResource(id = R.string.title_autofill),
                    onClick = rememberClickDebounced {
                        router.navigate(AutoFillSettingsDestination)
                    },
                    icon = { RightArrowIcon() },
                )
            }
        }

        item {
            Preference(
                text = stringResource(id = R.string.plugins),
                onClick = rememberClickDebounced { router.navigate(PluginsDestination) },
                icon = { RightArrowIcon() },
            )
        }

        item {
            Preference(
                text = stringResource(id = R.string.about),
                onClick = rememberClickDebounced { router.navigate(AboutDestination) },
                icon = { RightArrowIcon() },
            )
        }

        if (BuildConfig.DEBUG) {
            item {
                SectionHeader(
                    text = stringResource(id = R.string.debug),
                )

                Column {
                    resolver.widget(modifier = Modifier, widgetState = DebugSettingsWidgetState)
                }
            }
        }

    }


    AppBarStates(
        navigationIcon = {
            IconButton(onClick = rememberClickDebounced { router.back() }) { BackIcon() }
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


@Preview(device = Devices.PHONE)
@Composable
fun SettingsScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        SettingScreen()
    }
}