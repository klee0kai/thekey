@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.genpassw

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.github.klee0kai.thekey.app.ui.navigation.genHist
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storage.genpassw.model.GenPasswState
import com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreference
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickableDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetFaded
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun GenPasswordContent(
    modifier: Modifier = Modifier,
    dest: StorageDestination = StorageDestination(),
) {
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val scope = rememberCoroutineScope()
    val presenter by rememberOnScreenRef {
        DI.genPasswPresenter(dest.identifier())
            .also { it.init() }
    }
    val sliderValues = presenter!!.passwLenRange
    val state by presenter!!.state.collectAsState(key = Unit, initial = GenPasswState())
    val passw by rememberTargetFaded { state.passw }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(theme.colorScheme.androidColorScheme.background)
    ) {
        val (generateParams, passwText,
            histButton, generateButton, saveButton
        ) = createRefs()

        ConstraintLayout(
            modifier = Modifier
                .background(
                    color = theme.colorScheme.cardsBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 8.dp)
                .constrainAs(generateParams) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 0f,
                    )
                }
        ) {

            val (
                passwLenField,
                lenSliderField,
                simblsSwitchField,
                specSymbolsText,
            ) = createRefs()


            Text(
                text = stringResource(id = R.string.passw_len_is, state.passwLen),
                style = theme.typeScheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(passwLenField) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    top.linkTo(parent.top, margin = 16.dp)
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                    )
                }
            )

            Slider(
                value = state.passwLen.toFloat(),
                onValueChange = rememberClickArg { presenter?.input { copy(passwLen = it.toInt()) } },
                colors = SliderDefaults.colors(
                    thumbColor = theme.colorScheme.androidColorScheme.primary,
                    activeTrackColor = theme.colorScheme.androidColorScheme.primary,
                    inactiveTrackColor = theme.colorScheme.androidColorScheme.inverseSurface
                        .copy(alpha = 0.4f),
                ),
                steps = sliderValues.last - sliderValues.first - 1,
                valueRange = (sliderValues.first.toFloat()..sliderValues.last.toFloat()),
                modifier = Modifier
                    .constrainAs(lenSliderField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                        top.linkTo(passwLenField.bottom)
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            startMargin = 16.dp,
                            endMargin = 16.dp,
                        )
                    }
            )

            SwitchPreference(
                modifier = Modifier
                    .constrainAs(simblsSwitchField) {
                        width = Dimension.fillToConstraints
                        top.linkTo(lenSliderField.bottom, margin = 4.dp)
                        linkTo(start = parent.start, end = parent.end)
                    }
                    .rememberClickableDebounced(debounce = 50.milliseconds) {
                        presenter?.input { copy(symInPassw = !state.symInPassw) }
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.symbols_in_passw),
                checked = state.symInPassw,
            )

            SwitchPreference(
                modifier = Modifier
                    .constrainAs(specSymbolsText) {
                        width = Dimension.fillToConstraints
                        top.linkTo(simblsSwitchField.bottom, margin = 4.dp)
                        linkTo(start = parent.start, end = parent.end)
                    }
                    .rememberClickableDebounced(debounce = 50.milliseconds) {
                        presenter?.input { copy(specSymbolsInPassw = !state.specSymbolsInPassw) }
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.spec_in_passw),
                checked = state.specSymbolsInPassw,
            )
        }


        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(saveButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClick = rememberClickDebounced { presenter?.saveAsNewNote() }
        ) {
            Text(
                text = stringResource(R.string.save),
                style = theme.typeScheme.buttonText,
            )
        }


        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(generateButton) {
                    bottom.linkTo(saveButton.top, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            colors = theme.colorScheme.grayTextButtonColors,
            onClick = rememberClickDebounced { presenter?.generatePassw() },
        ) {
            Text(
                text = stringResource(R.string.passw_generate),
                style = theme.typeScheme.buttonText,
            )
        }

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(histButton) {
                    bottom.linkTo(generateButton.top, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            colors = theme.colorScheme.grayTextButtonColors,
            onClick = rememberClickDebounced { router?.navigate(dest.genHist()) }
        ) {
            Text(
                text = stringResource(R.string.hist),
                style = theme.typeScheme.buttonText,
            )
        }

        TextButton(
            modifier = Modifier
                .alpha(passw.alpha)
                .constrainAs(passwText) {
                    linkTo(
                        top = generateParams.bottom,
                        bottom = histButton.top,
                        start = parent.start,
                        end = parent.end
                    )
                },
            colors = theme.colorScheme.whiteTextButtonColors,
            onClick = rememberClickDebounced { presenter?.copyToClipboard() }
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = passw.current,
                style = theme.typeScheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }

}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun GenPasswordContentPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun genPasswPresenter(storageIdentifier: StorageIdentifier) =
            object : GenPasswPresenter {
                override val state = MutableStateFlow(
                    GenPasswState(
                        passwLen = 8,
                        symInPassw = true,
                        passw = "GE@#!1"
                    )
                )
            }
    })
    GenPasswordContent(
        dest = StorageDestination(
            path = Dummy.unicString,
            version = 2,
            selectedPage = 1,
        )
    )
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun GenPasswordContentPreviewScreenPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagePresenter(storageIdentifier: StorageIdentifier) =
            StoragePresenterDummy()
    })

    DebugDarkScreenPreview {
        StorageScreen(
            dest = StorageDestination(
                path = Dummy.unicString,
                version = 2,
                selectedPage = 1,
            )
        )
    }
}
