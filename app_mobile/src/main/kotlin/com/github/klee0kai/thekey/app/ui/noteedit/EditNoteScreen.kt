@file:OptIn(ExperimentalWearMaterialApi::class)

package com.github.klee0kai.thekey.app.ui.noteedit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs.Account
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs.Otp
import com.github.klee0kai.thekey.app.ui.noteedit.model.initTab
import com.github.klee0kai.thekey.app.ui.noteedit.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.noteedit.presenter.EditNotePresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.OtpMethod
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SecondaryTabs
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SecondaryTabsConst
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupDropDownField
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.DropDownField
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.possitions.toPx
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.TargetAlpha
import com.github.klee0kai.thekey.core.utils.views.animateSkeletonModifier
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.crossFadeAlpha
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.thenIf
import com.github.klee0kai.thekey.core.utils.views.topDp
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun EditNoteScreen(
    dest: EditNoteDestination = EditNoteDestination(),
) = Screen {
    val scope = rememberCoroutineScope()
    val router by LocalRouter.currentRef
    val view = LocalView.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef {
        DI.editNotePresenter(dest.identifier()).apply {
            init(dest.tab, dest.note, dest.otpNote)
        }
    }
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    val titles = listOf(stringResource(id = R.string.account), stringResource(id = R.string.otp))
    val state by presenter!!.state.collectAsState(
        key = Unit,
        initial = EditNoteState(isSkeleton = true)
    )
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable && !dest.isIgnoreRemove }
    val skeletonModifier by animateSkeletonModifier { state.isSkeleton }

    val pagerHeight = if (!state.isEditMode) SecondaryTabsConst.allHeight else 0.dp

    val pageSwipeState = rememberSwipeableState(dest.initTab())
    val page by rememberDerivedStateOf {
        runCatching { pageSwipeState.progress.crossFadeAlpha() }.getOrNull()
            ?: TargetAlpha(dest.tab, dest.tab, 1f)
    }
    val scrollState = rememberScrollState()
    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)

    BackHandler(state.otpMethodExpanded || state.otpAlgoExpanded || state.colorGroupExpanded) {
        presenter?.input {
            copy(
                otpMethodExpanded = false,
                otpAlgoExpanded = false,
                colorGroupExpanded = false,
            )
        }
    }
    LaunchedEffect(key1 = page.current) {
        if (!state.isSkeleton) {
            presenter?.input { copy(page = page.current) }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(theme.colorScheme.androidColorScheme.background)
            .imePadding()
            .verticalScroll(scrollState)
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp())
            .padding(bottom = safeContentPadding.calculateBottomPadding() + 16.dp)
            .padding(horizontal = safeContentPadding.horizontal(minValue = 16.dp))
            .padding(top = AppBarConst.appBarSize + pagerHeight + safeContentPadding.calculateTopPadding())
            .thenIf(!state.isEditMode) {
                swipeable(
                    state = pageSwipeState,
                    anchors = mapOf(0f to Account, -view.width.toFloat() to Otp),
                    thresholds = { _, _ -> FractionalThreshold(0.2f) },
                    orientation = Orientation.Horizontal
                )
            },
    ) {
        val (
            siteTextField, loginTextField,
            passwTextField, passwChangeDateField, descriptionTextField,
            otpTypeField, otpAlgoField,
            otpPeriodField, otpDigitsField,
            colorGroupField,
        ) = createRefs()

        AppTextField(
            modifier = Modifier
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
            isSkeleton = state.isSkeleton,
            value = state.siteOrIssuer,
            onValueChange = rememberClickArg { presenter?.input { copy(siteOrIssuer = it) } },
            label = {
                val text = if (page.current == Account) R.string.site else R.string.issuer
                Text(
                    modifier = Modifier
                        .alpha(page.alpha),
                    text = stringResource(text)
                )
            }
        )

        AppTextField(
            modifier = Modifier
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
            value = state.loginOrName,
            isSkeleton = state.isSkeleton,
            onValueChange = rememberClickArg { presenter?.input { copy(loginOrName = it) } },
            label = {
                val text = if (page.current == Account) R.string.login else R.string.name
                Text(
                    modifier = Modifier
                        .alpha(page.alpha),
                    text = stringResource(text)
                )
            }
        )

        AppTextField(
            modifier = Modifier
                .alpha(page.alpha)
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
            isSkeleton = state.isSkeleton,
            value = when (page.current) {
                Account -> state.passw
                Otp -> state.otpSecret
            },
            onValueChange = rememberClickArg {
                when (page.current) {
                    Account -> presenter?.input { copy(passw = it) }
                    Otp -> presenter?.input { copy(otpSecret = it) }
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
                onClick = rememberClickDebounced(dest) { presenter?.showHistory(router) },
                content = { Text(text = state.changeTime) }
            )
        }

        if (page.current == Account) {
            AppTextField(
                modifier = Modifier
                    .alpha(page.alpha)
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
                isSkeleton = state.isSkeleton,
                value = state.desc,
                onValueChange = rememberClickArg { presenter?.input { copy(desc = it) } },
                label = { Text(stringResource(R.string.description)) },
            )
        }

        if (page.current == Otp) {
            DropDownField(
                modifier = Modifier
                    .alpha(page.alpha)
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
                isSkeleton = state.isSkeleton,
                expanded = state.otpMethodExpanded,
                onExpandedChange = rememberClickArg { presenter?.input { copy(otpMethodExpanded = it) } },
                variants = state.otpMethodVariants,
                selectedIndex = state.otpMethodSelected,
                onSelected = rememberClickArg { selected ->
                    presenter?.input {
                        copy(
                            otpMethodSelected = selected,
                            otpMethodExpanded = false
                        )
                    }
                },
                label = { Text(stringResource(R.string.method)) }
            )

            DropDownField(
                modifier = Modifier
                    .alpha(page.alpha)
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
                isSkeleton = state.isSkeleton,
                expanded = state.otpAlgoExpanded,
                onExpandedChange = rememberClickArg { newExp ->
                    presenter?.input { copy(otpAlgoExpanded = newExp) }
                },
                variants = state.otpAlgoVariants,
                selectedIndex = state.otpAlgoSelected,
                onSelected = rememberClickArg { selected ->
                    presenter?.input { copy(otpAlgoSelected = selected, otpAlgoExpanded = false) }
                },
                label = { Text(stringResource(R.string.algorithm)) }
            )

            AppTextField(
                modifier = Modifier
                    .alpha(page.alpha)
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
                enabled = state.otpMethod() != OtpMethod.OTP,
                isSkeleton = state.isSkeleton,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = when (state.otpMethod()) {
                    OtpMethod.OTP -> ""
                    OtpMethod.HOTP -> state.otpCounter
                    OtpMethod.TOTP, OtpMethod.YAOTP -> state.otpInterval
                },
                onValueChange = rememberClickArg {
                    when (state.otpMethod()) {
                        OtpMethod.OTP -> Unit
                        OtpMethod.HOTP -> presenter?.input { copy(otpCounter = it) }
                        OtpMethod.TOTP, OtpMethod.YAOTP -> presenter?.input { copy(otpInterval = it) }
                    }
                },
                label = {
                    Text(
                        stringResource(
                            id = when (state.otpMethod()) {
                                OtpMethod.OTP, OtpMethod.HOTP -> R.string.counter
                                OtpMethod.TOTP, OtpMethod.YAOTP -> R.string.period
                            }
                        )
                    )
                },
            )


            AppTextField(
                modifier = Modifier
                    .alpha(page.alpha)
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
                isSkeleton = state.isSkeleton,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = state.otpDigits,
                onValueChange = rememberClickArg { presenter?.input { copy(otpDigits = it) } },
                label = {
                    Text(
                        stringResource(
                            id = when (state.otpMethod()) {
                                OtpMethod.OTP, OtpMethod.HOTP, OtpMethod.TOTP -> R.string.digits
                                OtpMethod.YAOTP -> R.string.symbols
                            }
                        )
                    )
                },
            )
        }

        ColorGroupDropDownField(
            modifier = Modifier
                .alpha(page.alpha)
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
            isSkeleton = state.isSkeleton,
            selectedIndex = state.colorGroupSelected,
            variants = state.colorGroupVariants,
            expanded = state.colorGroupExpanded,
            onExpandedChange = rememberClickArg { presenter?.input { copy(colorGroupExpanded = it) } },
            onSelected = rememberClickArg {
                presenter?.input {
                    copy(
                        colorGroupSelected = it,
                        colorGroupExpanded = false,
                    )
                }
            },
            label = { Text(stringResource(R.string.group)) }
        )
    }

    SecondaryTabs(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize + WindowInsets.safeContent.topDp),
        isVisible = !state.isEditMode && scrollState.value <= 0,
        titles = titles,
        selectedTab = page.current.ordinal,
        onTabClicked = rememberClickArg {
            scope.launch {
                pageSwipeState.animateTo(EditTabs.entries.getOrElse(it) { Account })
            }
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize + pagerHeight + safeContentPadding.calculateTopPadding(),
                bottom = 16.dp + safeContentPadding.calculateBottomPadding(),
                start = safeContentPadding.horizontal(minValue = 16.dp),
                end = safeContentPadding.horizontal(minValue = 16.dp),
            ),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (!imeIsVisibleAnimated.current) {
            TextButton(
                modifier = Modifier
                    .alpha(page.alpha)
                    .fillMaxWidth(),
                colors = LocalColorScheme.current.grayTextButtonColors,
                onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                    if (page.current == Account) {
                        presenter?.generate(router)
                    } else {
                        presenter?.scanQRCode(router)
                    }
                }
            ) {
                val textRes =
                    if (page.current == Account) R.string.passw_generate else R.string.qr_code_scan
                Text(stringResource(textRes))
            }
        }

        if (isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeIsVisibleAnimated.alpha)
                    .alpha(isSaveAvailable.alpha),
                onClick = rememberClickDebounced { presenter?.save(router) }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }

    AppBarStates(
        isVisible = scrollState.value <= 30.dp.toPx(),
        navigationIcon = {
            IconButton(onClick = rememberClickDebounced { router?.back() }) {
                BackMenuIcon()
            }
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit else R.string.create)) },
        actions = {
            if (isRemoveAvailable.current) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = rememberClickDebounced { presenter?.remove(router) }
                )
            }

            if (imeIsVisibleAnimated.current && isSaveAvailable.current) {
                DoneIconButton(
                    modifier = Modifier
                        .alpha(imeIsVisibleAnimated.alpha)
                        .alpha(isSaveAvailable.alpha),
                    onClick = rememberClickDebounced { presenter?.save(router) }
                )
            }
        }
    )
}


@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun CreateAccountScreenP6SkeletonPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isSkeleton = true,
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(dest = EditNoteDestination(path = Dummy.unicString))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun CreateOTPScreenP6SkeletonPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isSkeleton = true,
                )
            ) {

            }
    })
    DebugDarkScreenPreview {
        EditNoteScreen(dest = EditNoteDestination(path = Dummy.unicString, tab = Otp))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun CreateAccountScreenP6Preview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isSkeleton = false,
                    siteOrIssuer = "some.site.com",
                    loginOrName = "myLogin@2",
                    passw = "123#",
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(EditNoteDestination(path = Dummy.unicString))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun EditAccountScreenP6Preview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isEditMode = true,
                    isRemoveAvailable = true,
                    isSkeleton = false,
                    siteOrIssuer = "some.site.com",
                    loginOrName = "myLogin@2",
                    passw = "123#",
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(EditNoteDestination(path = Dummy.unicString))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun EditAccountScreenSaveP6Preview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isEditMode = true,
                    isRemoveAvailable = false,
                    isSkeleton = false,
                    isSaveAvailable = true,
                    siteOrIssuer = "some.site.com",
                    loginOrName = "myLogin@2",
                    passw = "123#",
                    colorGroupVariants = KeyColor.selectableColorGroups,
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(EditNoteDestination(path = Dummy.unicString))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun EditOTPScreenP6SkeletonPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isEditMode = true,
                    isSkeleton = true,
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(dest = EditNoteDestination(path = Dummy.unicString, tab = Otp))
    }
}

@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun EditOTPScreenP6Preview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter =
            object : EditNotePresenterDummy(
                EditNoteState(
                    isEditMode = true,
                    isRemoveAvailable = true,
                    isSkeleton = false,
                    siteOrIssuer = "some.site.com",
                    loginOrName = "myLogin@2",
                    otpSecret = "Ot#SecteXA",
                )
            ) {

            }
    })

    DebugDarkScreenPreview {
        EditNoteScreen(dest = EditNoteDestination(path = Dummy.unicString, tab = Otp))
    }
}