@file:OptIn(ExperimentalFoundationApi::class, ExperimentalWearMaterialApi::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields.ColorGroupDropDownField
import com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields.DropDownField
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs.Account
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs.Otp
import com.github.klee0kai.thekey.app.utils.views.TargetAlpha
import com.github.klee0kai.thekey.app.utils.views.crossFadeAlpha
import com.github.klee0kai.thekey.app.utils.views.currentViewSizeState
import com.github.klee0kai.thekey.app.utils.views.pxToDp
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.app.utils.views.rememberSkeletonModifier
import com.github.klee0kai.thekey.app.utils.views.rememberTargetAlphaCrossSade
import com.github.klee0kai.thekey.app.utils.views.toPx
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun EditNoteScreen(
    args: EditNoteDestination = EditNoteDestination(),
) {
    val scope = rememberCoroutineScope()
    val navigator = LocalRouter.current
    val view = LocalView.current
    val presenter = remember {
        DI.editNotePresenter(args.identifier()).apply {
            init(args.prefilled)
        }
    }
    val titles = listOf(stringResource(id = R.string.account), stringResource(id = R.string.otp))
    val state by presenter.state.collectAsState()
    val isSaveAvailable by rememberTargetAlphaCrossSade { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetAlphaCrossSade { state.isRemoveAvailable }
    val skeletonModifier by rememberSkeletonModifier { state.isSkeleton }

    val pagerHeight = if (!state.isEditMode) SecondaryTabsConst.allHeight else 0.dp

    val pageSwipeState = rememberSwipeableState(Account)
    val page by rememberDerivedStateOf {
        runCatching { pageSwipeState.progress.crossFadeAlpha() }.getOrNull()
            ?: TargetAlpha(Account, Account, 0f)
    }
    val scrollState = rememberScrollState()
    val viewSize by currentViewSizeState()
    val saveInToolbarAlpha by rememberTargetAlphaCrossSade { viewSize.height < 700.dp }

    BackHandler(state.otpTypeExpanded || state.otpAlgoExpanded) {
        presenter.state.update {
            it.copy(
                otpTypeExpanded = false,
                otpAlgoExpanded = false,
            )
        }
    }
    LaunchedEffect(key1 = page.current) {
        if (!state.isSkeleton) {
            presenter.input { copy(page = page.current) }
        }
    }

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
                anchors = mapOf(0f to Account, -view.width.toFloat() to Otp),
                thresholds = { _, _ -> FractionalThreshold(0.2f) },
                orientation = Orientation.Horizontal
            ),
    ) {
        val (
            siteTextField, loginTextField,
            passwTextField, passwChangeDateField, descriptionTextField,
            otpTypeField, otpAlgoField,
            otpPeriodField, otpDigitsField,
            colorGroupField,
        ) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .then(skeletonModifier)
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
            value = state.siteOrIssuer,
            onValueChange = { presenter.input { copy(siteOrIssuer = it) } },
            label = {
                val text = if (page.current == Account) R.string.site else R.string.issuer
                Text(
                    modifier = Modifier
                        .alpha(page.alpha),
                    text = stringResource(text)
                )
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .then(skeletonModifier)
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
            value = state.login,
            onValueChange = { presenter.input { copy(login = it) } },
            label = {
                val text = if (page.current == Account) R.string.login else R.string.name
                Text(
                    modifier = Modifier
                        .alpha(page.alpha),
                    text = stringResource(text)
                )
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .alpha(page.alpha)
                .then(skeletonModifier)
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
            value = when (page.current) {
                Account -> state.passw
                Otp -> state.otpSecret
            },
            onValueChange = {
                when (page.current) {
                    Account -> presenter.input { copy(passw = it) }
                    Otp -> presenter.input { copy(otpSecret = it) }
                }

            },
            label = {
                Text(
                    text = when (page.current) {
                        Account -> stringResource(id = R.string.password)
                        Otp -> stringResource(id = R.string.secret)
                    }
                )
            }
        )

        if (state.changeTime.isNotBlank()) {
            TextButton(
                modifier = Modifier
                    .then(skeletonModifier)
                    .constrainAs(passwChangeDateField) {
                        linkTo(
                            top = passwTextField.bottom,
                            start = parent.start,
                            end = parent.end,
                            bottom = parent.bottom,
                            horizontalBias = 1f,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                        )
                    },
                onClick = { presenter.showHistory() },
                content = { Text(text = state.changeTime) }
            )
        }

        if (page.current == Account) {
            OutlinedTextField(
                modifier = Modifier
                    .alpha(page.alpha)
                    .then(skeletonModifier)
                    .constrainAs(descriptionTextField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = when {
                                state.changeTime.isNotBlank() -> passwChangeDateField.bottom
                                else -> passwTextField.bottom
                            },
                            start = parent.start,
                            end = parent.end,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                        )
                    },
                value = state.desc,
                onValueChange = { presenter.input { copy(desc = it) } },
                label = { Text(stringResource(R.string.description)) },
            )
        }

        if (page.current == Otp) {
            DropDownField(
                modifier = Modifier
                    .alpha(page.alpha)
                    .then(skeletonModifier)
                    .constrainAs(otpTypeField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = passwTextField.bottom,
                            start = parent.start,
                            end = otpAlgoField.start,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            endMargin = 8.dp,
                        )
                    },
                expanded = state.otpTypeExpanded,
                onExpandedChange = { presenter.input { copy(otpTypeExpanded = it) } },
                variants = state.otpTypeVariants,
                selectedIndex = state.otpTypeSelected,
                onSelected = { selected ->
                    presenter.input { copy(otpTypeSelected = selected, otpTypeExpanded = false) }
                },
                label = { Text(stringResource(R.string.type)) }
            )

            DropDownField(
                modifier = Modifier
                    .alpha(page.alpha)
                    .then(skeletonModifier)
                    .constrainAs(otpAlgoField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = passwTextField.bottom,
                            start = otpTypeField.end,
                            end = parent.end,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            startMargin = 8.dp,
                        )
                    },
                expanded = state.otpAlgoExpanded,
                onExpandedChange = { newExp -> presenter.input { copy(otpAlgoExpanded = newExp) } },
                variants = state.otpAlgoVariants,
                selectedIndex = state.otpAlgoSelected,
                onSelected = { selected ->
                    presenter.input { copy(otpAlgoSelected = selected, otpAlgoExpanded = false) }
                },
                label = { Text(stringResource(R.string.algorithm)) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .alpha(page.alpha)
                    .then(skeletonModifier)
                    .constrainAs(otpPeriodField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = otpTypeField.bottom,
                            start = parent.start,
                            end = otpDigitsField.start,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            endMargin = 8.dp,
                        )
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = state.otpPeriod,
                onValueChange = { presenter.input { copy(otpPeriod = it) } },
                label = { Text(stringResource(R.string.period)) },
            )


            OutlinedTextField(
                modifier = Modifier
                    .alpha(page.alpha)
                    .then(skeletonModifier)
                    .constrainAs(otpDigitsField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = otpTypeField.bottom,
                            start = otpPeriodField.end,
                            end = parent.end,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp,
                            startMargin = 8.dp,
                        )
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = state.otpDigits,
                onValueChange = { presenter.input { copy(otpDigits = it) } },
                label = { Text(stringResource(R.string.digits)) },
            )
        }

        ColorGroupDropDownField(
            modifier = Modifier
                .alpha(page.alpha)
                .then(skeletonModifier)
                .fillMaxWidth(0.5f)
                .constrainAs(colorGroupField) {
                    linkTo(
                        top = when (page.current) {
                            Account -> descriptionTextField.bottom
                            Otp -> otpPeriodField.bottom
                        },
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            selectedIndex = state.colorGroupSelected,
            variants = state.colorGroupVariants,
            expanded = state.colorGroupExpanded,
            onExpandedChange = { presenter.input { copy(colorGroupExpanded = it) } },
            onSelected = { presenter.input { copy(colorGroupSelected = it, colorGroupExpanded = false) } },
            label = { Text(stringResource(R.string.group)) }
        )

    }


    SecondaryTabs(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize),
        isVisible = !state.isEditMode && scrollState.value <= 0,
        titles = titles,
        selectedTab = page.current.ordinal,
        onTabClicked = {
            scope.launch {
                pageSwipeState.animateTo(EditTabs.entries.getOrElse(it) { Account })
            }
        },
    )

    if (!saveInToolbarAlpha.current) {
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
                    .alpha(page.alpha)
                    .fillMaxWidth(),
                onClick = {
                    if (page.current == Account) {
                        presenter.generate()
                    } else {
                        presenter.scanQRCode()
                    }
                }
            ) {
                val textRes = if (page.current == Account) R.string.passw_generate else R.string.qr_code_scan
                Text(stringResource(textRes))
            }

            if (isSaveAvailable.current) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(saveInToolbarAlpha.alpha)
                        .alpha(isSaveAvailable.alpha),
                    onClick = { presenter.save() }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }


    AppBarStates(
        isVisible = scrollState.value <= 30.dp.toPx(),
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit else R.string.create)) },
        actions = {
            if (isRemoveAvailable.current) {
                IconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = { presenter.tryRemove() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.remove),
                        tint = LocalColorScheme.current.deleteColor
                    )
                }
            }

            if (saveInToolbarAlpha.current && isSaveAvailable.current) {
                IconButton(
                    modifier = Modifier
                        .alpha(saveInToolbarAlpha.alpha)
                        .alpha(isSaveAvailable.alpha),
                    onClick = { presenter.save() }
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