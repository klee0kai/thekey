@file:OptIn(ExperimentalFoundationApi::class, ExperimentalWearMaterialApi::class)

package com.github.klee0kai.thekey.app.ui.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.utils.views.AutoFillList
import com.github.klee0kai.thekey.app.utils.views.TargetAlpha
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.crossFadeAlpha
import com.github.klee0kai.thekey.app.utils.views.currentViewSizeState
import com.github.klee0kai.thekey.app.utils.views.pxToDp
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.app.utils.views.skeleton
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun NoteScreen(
    args: NoteDestination = NoteDestination(),
) {
    val scope = rememberCoroutineScope()
    val navigator = LocalRouter.current
    val view = LocalView.current
    val presenter = remember {
        DI.notePresenter(args.identifier()).apply {
            init(args.prefilled)
        }
    }
    val isEditNote = args.notePtr != 0L
    val titles = listOf(stringResource(id = R.string.account), stringResource(id = R.string.otp))
    val note by presenter.note.collectAsState(key = Unit)
    val originNote = presenter.originNote.collectAsStateCrossFaded()
    val pagerHeight = if (!isEditNote) SecondaryTabsConst.allHeight else 0.dp

    val pageSwipeState = rememberSwipeableState(0)
    val targetPage by rememberDerivedStateOf { runCatching { pageSwipeState.progress.crossFadeAlpha() }.getOrNull() ?: TargetAlpha(0, 0, 0f) }
    val isAccountEditPageTarget by rememberDerivedStateOf { targetPage.current == 0 }

    val isSkeleton = rememberDerivedStateOf { isEditNote && originNote.value.current == null }
    val alpha = rememberDerivedStateOf { if (isEditNote) originNote.value.alpha else 1f }
    val scrollState = rememberScrollState()
    val viewSize by currentViewSizeState()
    val bottomButtons = rememberDerivedStateOf { viewSize.height > 700.dp }
    val saveInToolbarAlpha = animateAlphaAsState(!bottomButtons.value)
    var otpTypeSelected by remember { mutableStateOf<Boolean>(false) }

    ConstraintLayout(
        optimizationLevel = 0,
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp())
            .padding(
                top = 16.dp + AppBarConst.appBarSize + pagerHeight,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
            .swipeable(
                state = pageSwipeState,
                anchors = mapOf(0f to 0, -view.width.toFloat() to 1),
                thresholds = { _, _ -> FractionalThreshold(0.2f) },
                orientation = Orientation.Horizontal
            ),
    ) {
        val (
            siteTextField, loginTextField,
            passwTextField, descriptionTextField,
            otpTypeField, otpTypeAutoFillList,
            otpAlgoField, otpAlgoAutoFill,
            otpPeriod, otpDigits,
        ) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(siteTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.site,
            onValueChange = { presenter.note.value = note.copy(site = it) },
            label = { Text(stringResource(R.string.site)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(loginTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = siteTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.login,
            onValueChange = { presenter.note.value = note.copy(login = it) },
            label = { Text(stringResource(R.string.login)) }
        )


        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .alpha(targetPage.alpha)
                .skeleton(isSkeleton.value)
                .constrainAs(passwTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = loginTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.passw,
            onValueChange = { presenter.note.value = note.copy(passw = it) },
            label = {
                val text = if (isAccountEditPageTarget) R.string.password else R.string.secret
                Text(stringResource(text))
            }
        )


        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(descriptionTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = passwTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.desc,
            onValueChange = { presenter.note.value = note.copy(desc = it) },
            label = { Text(stringResource(R.string.description)) }
        )


//          otpTypeField, otpTypeAutoFillList,
//            otpAlgoField, otpAlgoAutoFill,
//            otpPeriod, otpDigits,

        if (!isAccountEditPageTarget) {
            OutlinedTextField(
                modifier = Modifier
                    .alpha(targetPage.alpha)
                    .skeleton(isSkeleton.value)
//                .menuAnchor()
                    .onFocusChanged { otpTypeSelected = it.isFocused }
                    .constrainAs(otpTypeField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = descriptionTextField.bottom,
                            start = parent.start,
                            end = otpAlgoField.start,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            endMargin = 8.dp,
                        )
                    },
                readOnly = true,
                value = "type",
                onValueChange = { },
                label = { }
            )

            OutlinedTextField(
                modifier = Modifier
                    .alpha(targetPage.alpha)
                    .skeleton(isSkeleton.value)
                    .constrainAs(otpAlgoField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = descriptionTextField.bottom,
                            start = otpTypeField.end,
                            end = parent.end,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            endMargin = 8.dp,
                        )
                    },
                value = "type",
                onValueChange = { },
                label = { }
            )

            AutoFillList(
                modifier = Modifier
                    .constrainAs(otpTypeAutoFillList) {
                        height = Dimension.wrapContent
                        width = Dimension.fillToConstraints
                        linkTo(
                            start = otpTypeField.start,
                            end = otpTypeField.end,
                            top = otpTypeField.bottom,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            bottomMargin = 16.dp
                        )
                    },
                isVisible = otpTypeSelected,
                variants = listOf("otp", "totp", "yaotp"),
                onSelected = { selected ->

                }
            )
        }
    }


    SecondaryTabs(
        modifier = Modifier,
        isVisible = !isEditNote && scrollState.value == 0,
        titles = titles,
        selectedTab = targetPage.current,
        onTabClicked = { scope.launch { pageSwipeState.animateTo(it) } },
    )

    if (bottomButtons.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp + AppBarConst.appBarSize + pagerHeight,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                modifier = Modifier
                    .alpha(targetPage.alpha)
                    .fillMaxWidth(),
                onClick = {
                    if (isAccountEditPageTarget) {
                        presenter.generate()
                    } else {

                    }
                }
            ) {
                val textRes = if (isAccountEditPageTarget) R.string.passw_generate else R.string.qr_code_scan
                Text(stringResource(textRes))
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { presenter.save() }
            ) {
                Text(stringResource(R.string.save))
            }

        }
    }


    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = {
            Text(
                text = stringResource(
                    id = if (isEditNote) R.string.edit else R.string.create
                )
            )
        },
        actions = {
            if (saveInToolbarAlpha.value > 0) {
                IconButton(
                    modifier = Modifier.alpha(saveInToolbarAlpha.value),
                    onClick = {
                        presenter.save()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.save),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}