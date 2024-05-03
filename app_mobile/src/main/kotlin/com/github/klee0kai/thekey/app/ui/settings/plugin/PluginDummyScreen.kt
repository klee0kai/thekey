package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardReset
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallError
import com.github.klee0kai.thekey.app.features.model.Installed
import com.github.klee0kai.thekey.app.features.model.Installing
import com.github.klee0kai.thekey.app.features.model.NotInstalled
import com.github.klee0kai.thekey.app.features.model.isNotInstalled
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.model.DynamicDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.app.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun PluginDummyScreen(dest: DynamicDestination) {
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.pluginPresenter(dest.feature) }
    val feature = dest.feature
    val featureStatus by presenter!!.status.collectAsState(key = Unit, initial = NotInstalled)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        val (
            descField,
            installField,
            statusField,
        ) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(descField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                        verticalBias = 0.1f,
                        topMargin = 16.dp,
                        bottomMargin = 16.dp,
                        startMargin = 30.dp,
                        endMargin = 30.dp,
                    )
                },
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            text = stringResource(id = feature.descRes)
        )

        if (featureStatus.isNotInstalled) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(installField) {
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            top = parent.top,
                            bottom = parent.bottom,
                            verticalBias = 1f,
                            horizontalBias = 1f,
                        )
                    },
                onClick = {
                    when (featureStatus) {
                        NotInstalled, InstallError -> presenter?.install()
                        Installed, is Installing -> presenter?.uninstall()
                    }
                },
                colors = when (featureStatus) {
                    InstallError, Installed, is Installing -> LocalColorScheme.current.textButtonColors
                    InstallError, NotInstalled -> ButtonDefaults.textButtonColors()
                }
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(
                        id = when (featureStatus) {
                            InstallError -> R.string.try_again
                            Installed -> R.string.uninstall
                            is Installing -> R.string.cancel
                            NotInstalled ->
                                if (feature.purchase.isBlank()) {
                                    R.string.install
                                } else {
                                    R.string.buy
                                }
                        }
                    )
                )
            }
        }

        Text(
            modifier = Modifier
                .constrainAs(statusField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                        verticalBias = 0.8f,
                    )
                },
            text = "status $featureStatus"
        )
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        },
        titleContent = {
            Text(
                text = stringResource(id = feature.titleRes)
            )
        },
    )
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun PluginDummyScreenScreenPreview() = AppTheme {
    DI.hardReset()
    PluginDummyScreen(QRCodeScanDestination)
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun PluginDummyScreenInstallingPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
            override val status = MutableStateFlow(Installing(progress = 0.6f))
        }
    })
    PluginDummyScreen(QRCodeScanDestination)
}

@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun PluginDummyScreenInstalledPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
            override val status = MutableStateFlow(Installed)
        }
    })
    PluginDummyScreen(QRCodeScanDestination)
}

@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun PluginDummyScreenInstallErrorPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
            override val status = MutableStateFlow(InstallError)
        }
    })
    PluginDummyScreen(QRCodeScanDestination)
}