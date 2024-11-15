package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
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
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.app.ui.settings.plugin.presenter.PluginPresenter
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallError
import com.github.klee0kai.thekey.core.domain.model.feature.model.Installed
import com.github.klee0kai.thekey.core.domain.model.feature.model.Installing
import com.github.klee0kai.thekey.core.domain.model.feature.model.NotInstalled
import com.github.klee0kai.thekey.core.domain.model.feature.model.isNotInstalled
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.DynamicDestination
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun PluginDummyScreen(dest: DynamicDestination) {
    val theme = LocalTheme.current
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.pluginPresenter(dest.feature) }
    val feature = dest.feature
    val featureStatus by presenter!!.status.collectAsState(key = Unit, initial = NotInstalled)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent.minInsets(16.dp))
            .padding(top = AppBarConst.appBarSize),
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
                    linkToParent(
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
                        linkToParent(
                            verticalBias = 1f,
                            horizontalBias = 1f,
                        )
                    },
                onClick = {
                    when (featureStatus) {
                        NotInstalled, InstallError -> presenter?.install(router)
                        Installed, is Installing -> presenter?.uninstall(router)
                    }
                },
                colors = when (featureStatus) {
                    InstallError, Installed, is Installing -> theme.colorScheme.grayTextButtonColors
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
                    ),
                    style = theme.typeScheme.buttonText,
                )
            }
        }

        Text(
            modifier = Modifier
                .constrainAs(statusField) {
                    linkToParent(
                        verticalBias = 0.8f,
                    )
                },
            text = "status $featureStatus"
        )
    }

    AppBarStates(
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = { Text(text = stringResource(id = feature.titleRes)) },
    )
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun PluginDummyScreenScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        PluginDummyScreen(QRCodeScanDestination)
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun PluginDummyScreenInstallingPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
                override val status = MutableStateFlow(Installing(progress = 0.6f))
            }
        })
        PluginDummyScreen(QRCodeScanDestination)
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun PluginDummyScreenInstalledPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
                override val status = MutableStateFlow(Installed)
            }
        })
        PluginDummyScreen(QRCodeScanDestination)
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun PluginDummyScreenInstallErrorPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun pluginPresenter(feature: DynamicFeature) = object : PluginPresenter {
                override val status = MutableStateFlow(InstallError)
            }
        })
        PluginDummyScreen(QRCodeScanDestination)
    }
}