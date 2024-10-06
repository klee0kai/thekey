package com.github.klee0kai.thekey.app.ui.settings.plugins

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
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.features.visiblePlugins
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallDynamicFeature
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.RightArrowIcon
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.truncate
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun PluginsScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.pluginsPresenter() }
    val features by presenter!!.features.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize(),
        contentPadding = WindowInsets.safeContent
            .truncate(right = true, left = true)
            .asPaddingValues(),
    ) {
        features.forEach { feature ->
            item {
                Preference(
                    text = stringResource(id = feature.feature.titleRes),
                    onClick = rememberClickDebounced { router.navigate(PluginDestination(feature = feature.feature)) },
                    icon = { RightArrowIcon() },
                )
            }
        }
    }


    AppBarStates(
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = { Text(text = stringResource(id = R.string.plugins)) }
    )
}


@Preview(device = Devices.PHONE)
@Composable
fun PluginsScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.initPresenterModule(object : PresentersModule {
            override fun pluginsPresenter() = object : PluginsPresenter {
                override val features = MutableStateFlow(
                    DynamicFeature
                        .visiblePlugins()
                        .map { InstallDynamicFeature(it) }
                )
            }
        })
        PluginsScreen()
    }
}