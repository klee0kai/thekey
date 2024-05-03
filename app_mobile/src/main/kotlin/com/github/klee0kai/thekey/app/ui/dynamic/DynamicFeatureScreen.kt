package com.github.klee0kai.thekey.app.ui.dynamic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.dynamic.presenter.DynamicFeaturePresenter
import com.github.klee0kai.thekey.app.ui.navigation.model.DynamicDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.app.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun DynamicFeatureScreen(dest: DynamicDestination) {

    val presenter by rememberOnScreenRef { DI.dynamicFeaturePresenter(dest.feature) }
    val status by presenter!!.status.collectAsStateCrossFaded(key = Unit, initial = null)

    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val (
            titleField,
            descField,
            statusField,
            buttonField,
        ) = createRefs()

        Text(
            modifier = Modifier.constrainAs(titleField) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = parent.bottom,
                    verticalBias = 0.2f,
                    bottomMargin = 16.dp,
                )
            },
            text = stringResource(id = dest.feature.titleRes),
        )

        Text(
            modifier = Modifier.constrainAs(descField) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = parent.bottom,
                    verticalBias = 0.5f,
                    bottomMargin = 16.dp,
                )
            },
            text = stringResource(id = dest.feature.descRes),
        )

        when (val curStatus = status.current) {
            InstallError -> {
                Text(
                    modifier = Modifier
                        .alpha(status.alpha)
                        .constrainAs(statusField) {
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = parent.bottom,
                                verticalBias = 1f,
                                bottomMargin = 16.dp,
                            )
                        },
                    text = stringResource(id = R.string.install_error)
                )
            }

            Installed -> {
                Text(
                    modifier = Modifier
                        .alpha(status.alpha)
                        .constrainAs(statusField) {
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = parent.bottom,
                                verticalBias = 1f,
                                bottomMargin = 16.dp,
                            )
                        },
                    text = stringResource(id = R.string.installed)
                )
            }

            is Installing -> {
                Text(
                    modifier = Modifier
                        .alpha(status.alpha)
                        .constrainAs(statusField) {
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = parent.bottom,
                                verticalBias = 1f,
                                bottomMargin = 16.dp,
                            )
                        },
                    text = stringResource(id = R.string.installing_progress, curStatus.progress)
                )
            }

            NotInstalled ->
                TextButton(
                    modifier = Modifier
                        .alpha(status.alpha)
                        .constrainAs(buttonField) {
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = parent.bottom,
                                verticalBias = 1f,
                                bottomMargin = 16.dp,
                            )
                        },
                    onClick = {
                        presenter?.install()
                    },
                ) {
                    Text(text = stringResource(id = R.string.install))
                }

            null -> Unit
        }

    }
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun DynamicFeatureScreenPreview() = AppTheme {
    DI.hardReset()
    DynamicFeatureScreen(QRCodeScanDestination)
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun DynamicFeatureScreenInstallingPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun dynamicFeaturePresenter(feature: DynamicFeature) = object : DynamicFeaturePresenter {
            override val status = MutableStateFlow(Installing(progress = 0.6f))
        }
    })
    DynamicFeatureScreen(QRCodeScanDestination)
}

@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun DynamicFeatureScreenInstalledPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun dynamicFeaturePresenter(feature: DynamicFeature) = object : DynamicFeaturePresenter {
            override val status = MutableStateFlow(Installed)
        }
    })
    DynamicFeatureScreen(QRCodeScanDestination)
}

@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun DynamicFeatureScreenInstallErrorPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun dynamicFeaturePresenter(feature: DynamicFeature) = object : DynamicFeaturePresenter {
            override val status = MutableStateFlow(InstallError)
        }
    })
    DynamicFeatureScreen(QRCodeScanDestination)
}