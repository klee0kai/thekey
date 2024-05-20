package com.github.klee0kai.thekey.app.ui.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
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
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SectionHeader
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreference
import com.github.klee0kai.thekey.core.utils.views.truncate
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.launch

@Composable
fun SettingScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current

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
            SectionHeader(
                text = stringResource(id = R.string.other)
            )
        }

        item {
            Preference(
                text = stringResource(id = R.string.plugins),
                onClick = {
                    router.navigate(PluginsDestination)
                }
            )
        }

    }


    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { scope.launch { router.back() } }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
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
    AppTheme {
        SettingScreen()
    }
}