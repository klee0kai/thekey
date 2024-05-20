package com.github.klee0kai.thekey.app.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Text
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.preview.PreviewDevices
import com.github.klee0kai.thekey.core.utils.views.toPx
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun AboutScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val viewWidth = LocalView.current.width
    val colorScheme = LocalColorScheme.current
    val keysCount = max(10, (viewWidth / 90.dp.toPx()).toInt())

    ConstraintLayout(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize(),
    ) {
        val (
            centerField,
            bgField,
            bgKeysField,
            securePreTextField,
            secureField,
            versionFiled,
            developerField,
            developerNameField,
            designerField,
            designerNameField,
        ) = createRefs()

        Spacer(modifier = Modifier
            .size(0.dp)
            .constrainAs(centerField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
                )
            })

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bgField) {
                    height = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = developerNameField.top,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 0f,
                        bottomMargin = 40.dp,
                    )
                }
                .paint(
                    painterResource(id = R.drawable.bg_metallic),
                    contentScale = ContentScale.FillBounds
                )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-20).dp)
                .constrainAs(bgKeysField) {
                    linkTo(
                        top = bgField.top,
                        bottom = bgField.bottom,
                        start = bgField.start,
                        end = bgField.end,
                        verticalBias = 0.2f,
                    )
                },
        ) {
            repeat(keysCount) { index ->
                val keyCountMultiplied = keysCount * 0.8f
                item {
                    Image(
                        painterResource(id = R.drawable.key_to_right),
                        modifier = Modifier.alpha(
                            ((keyCountMultiplied - index) / keyCountMultiplied)
                                .coerceIn(0f, 0.7f)
                        ),
                        contentDescription = null,
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(securePreTextField) {
                    linkTo(
                        top = bgField.top,
                        bottom = secureField.top,
                        start = secureField.start,
                        end = secureField.end,
                        verticalBias = 1f,
                        horizontalBias = 0f,
                        bottomMargin = 12.dp,
                    )
                },
            text = stringResource(id = R.string.make_operations),
            color = colorScheme.androidColorScheme.primary,
        )

        Image(
            painterResource(id = R.drawable.secure),
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(secureField) {
                    linkTo(
                        top = bgField.top,
                        bottom = bgField.bottom,
                        start = bgField.start,
                        end = bgField.end,
                        verticalBias = 0.7f,
                        horizontalBias = 0f,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                    )
                },
            contentDescription = null,
        )

        Text(
            text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.labelMedium
                .copy(color = colorScheme.hintTextColor),
            modifier = Modifier
                .constrainAs(versionFiled) {
                    linkTo(
                        top = bgField.top,
                        bottom = securePreTextField.top,
                        start = bgField.start,
                        end = bgField.end,
                        verticalBias = 0.1f,
                        horizontalBias = 0f,
                        topMargin = 16.dp,
                        startMargin = 16.dp,
                    )
                }
        )

        Text(
            text = stringResource(id = R.string.developer),
            modifier = Modifier.constrainAs(developerField) {
                linkTo(
                    top = developerNameField.top,
                    bottom = developerNameField.bottom,
                    start = bgField.start,
                    end = bgField.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    horizontalBias = 0.2f,
                )
            }
        )


        Text(
            text = "Andrey Kuzubov\nGithub klee0kai",
            modifier = Modifier.constrainAs(developerNameField) {
                linkTo(
                    top = parent.top,
                    bottom = designerNameField.top,
                    start = centerField.start,
                    end = bgField.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    verticalBias = 1f,
                    horizontalBias = 0f,
                    bottomMargin = 16.dp,
                )
            }
        )


        Text(
            text = stringResource(id = R.string.designer),
            modifier = Modifier.constrainAs(designerField) {
                linkTo(
                    top = designerNameField.top,
                    bottom = designerNameField.bottom,
                    start = bgField.start,
                    end = bgField.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    horizontalBias = 0.2f,
                )
            }
        )


        Text(
            text = "Ekaterina Kuzubova\nBehance Katerina Shers",
            modifier = Modifier.constrainAs(designerNameField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = centerField.start,
                    end = bgField.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    horizontalBias = 0f,
                    bottomMargin = 16.dp,
                    topMargin = 16.dp,
                    verticalBias = 0.9f,
                )
            }
        )

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
                text = stringResource(id = R.string.about)
            )
        }
    )
}


@Composable
@Preview(device = Devices.PHONE)
fun AboutScreenPreview() = EdgeToEdgeTemplate {
    AppTheme { AboutScreen() }
}

@Composable
@Preview(device = PreviewDevices.PNOTE_LAND)
fun AboutScreenLandPreview() = EdgeToEdgeTemplate {
    AppTheme { AboutScreen() }
}



