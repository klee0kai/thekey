@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.genpassw

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.createNote
import com.github.klee0kai.thekey.app.ui.navigation.genHist
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded

@Preview
@Composable
fun GenPasswordContent(
    modifier: Modifier = Modifier,
    dest: StorageDestination = StorageDestination(),
) {
    val scope = rememberCoroutineScope()
    val presenter = remember {
        DI.genPasswPresenter(dest.identifier())
            .also { it.init() }
    }
    val router = LocalRouter.current
    val sliderValues = presenter.passwLenRange
    val lenSliderPosition by presenter.passwLen.collectAsState()
    val symbolsChecked by presenter.symInPassw.collectAsState()
    val specSymbolsChecked by presenter.specSymbolsInPassw.collectAsState()
    val passw by presenter.passw.collectAsStateCrossFaded()

    ConstraintLayout(
        modifier = modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (generateParams, passwText,
            histButton, generateButton, saveButton
        ) = createRefs()

        ConstraintLayout(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(top = 22.dp, bottom = 22.dp, start = 16.dp, end = 16.dp)
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

            val (lenText, lenSlider,
                symbolsText, symbolsSwitch,
                specSymbolsText, specSymbolsSwitch
            ) = createRefs()


            Text(
                text = lenSliderPosition.toString(),
                modifier = Modifier.constrainAs(lenText) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    top.linkTo(parent.top, margin = 16.dp)
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                    )
                }
            )

            Slider(
                value = lenSliderPosition.toFloat(),
                onValueChange = { presenter.passwLen.value = it.toInt() },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
                ),
                steps = sliderValues.last - sliderValues.first - 1,
                valueRange = (sliderValues.first.toFloat()..sliderValues.last.toFloat()),
                modifier = Modifier
                    .constrainAs(lenSlider) {
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                        top.linkTo(lenText.bottom, margin = 4.dp)
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                        )
                    }
            )

            Text(text = stringResource(id = R.string.symbols_in_passw),
                modifier = Modifier.constrainAs(symbolsText) {
                    top.linkTo(lenSlider.bottom, margin = 16.dp)
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        bias = 0f,
                    )
                })

            Switch(checked = symbolsChecked,
                onCheckedChange = { presenter.symInPassw.value = it },
                modifier = Modifier.constrainAs(symbolsSwitch) {
                    linkTo(
                        start = symbolsText.end,
                        end = parent.end,
                        top = symbolsText.top,
                        bottom = symbolsText.bottom,
                        horizontalBias = 1f,
                    )
                }
            )


            Text(text = stringResource(id = R.string.spec_in_passw),
                modifier = Modifier.constrainAs(specSymbolsText) {
                    top.linkTo(symbolsText.bottom, margin = 22.dp)
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        bias = 0f,
                    )
                }
            )


            Switch(checked = specSymbolsChecked,
                onCheckedChange = { presenter.specSymbolsInPassw.value = it },
                modifier = Modifier
                    .constrainAs(specSymbolsSwitch) {
                    linkTo(
                        start = specSymbolsText.end,
                        end = parent.end,
                        top = specSymbolsText.top,
                        bottom = specSymbolsText.bottom,
                        horizontalBias = 1f,
                    )
                }
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
            onClick = {
                router.navigate(dest.createNote(DecryptedNote(passw = presenter.passw.value)))
            }
        ) {
            Text(stringResource(R.string.save))
        }


        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(generateButton) {
                    bottom.linkTo(saveButton.top, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClick = {
                presenter.generatePassw()
            },
        ) {
            Text(stringResource(R.string.passw_generate))
        }

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(histButton) {
                    bottom.linkTo(generateButton.top, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClick = { router.navigate(dest.genHist()) }
        ) { Text(stringResource(R.string.hist)) }

        Text(
            text = passw.target,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .alpha(passw.alpha)
                .clickable { presenter.copyToClipboard() }
                .constrainAs(passwText) {
                    linkTo(
                        top = generateParams.bottom,
                        bottom = histButton.top,
                        start = parent.start,
                        end = parent.end
                    )
                }
        )

    }

}