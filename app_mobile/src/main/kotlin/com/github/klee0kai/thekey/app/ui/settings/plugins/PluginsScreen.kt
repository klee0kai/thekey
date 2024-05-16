package com.github.klee0kai.thekey.app.ui.settings.plugins

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.settings.Preference
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.ui.settings.plugins.presenter.PluginsPresenter
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun PluginsScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.pluginsPresenter() }
    val features by presenter!!.features.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize(),
    ) {
        features.forEach { feature ->
            item {
                Preference(
                    text = stringResource(id = feature.feature.titleRes),
                    onClick = { router.navigate(PluginDestination(feature = feature.feature)) }
                )
            }
        }
    }


    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { scope.launch { router.showNavigationBoard() } }) {
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
                text = stringResource(id = R.string.plugins)
            )
        }
    )
}


@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_6,
)
@Composable
fun PluginsScreenPreview() = AppTheme {
    DI.initPresenterModule(object : PresentersModule {
        override fun pluginsPresenter() = object : PluginsPresenter {
            override val features = MutableStateFlow(
                DynamicFeature
                    .allFeatures()
                    .map { InstallDynamicFeature(it) }
            )
        }
    })
    PluginsScreen()
}