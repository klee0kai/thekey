@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.otpnote

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.ui.otpnote.presenter.OtpNotePresenter
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.dummyLoaded
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.BottomSheetBigDialog
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.rememberSafeBottomSheetScaffoldState
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun OtpNoteDialog(
    dest: NoteDestination = NoteDestination(),
    initialValue: SheetValue = SheetValue.Hidden,
) {
    val router by LocalRouter.currentRef
    val colorScheme = LocalColorScheme.current
    val theme = LocalTheme.current
    val scope = rememberCoroutineScope()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isIme = WindowInsets.isIme
    val presenter by rememberOnScreenRef { DI.otpNotePresenter(dest.identifier()).apply { init() } }
    val note by presenter!!.note.collectAsState(
        key = Unit,
        initial = ColoredOtpNote(isLoaded = false)
    )

    val scaffoldState = rememberSafeBottomSheetScaffoldState(
        initialValue = initialValue,
        skipHiddenState = false,
    )
    var dragProgress by remember { mutableFloatStateOf(if (initialValue == SheetValue.PartiallyExpanded) 1f else 0f) }
    val backgroundColor by rememberDerivedStateOf {
        colorScheme
            .androidColorScheme
            .background
            .copy(alpha = ((1f - dragProgress) + 0.4f).coerceIn(0f, 1f))
    }
    val imeAnimated by animateTargetCrossFaded(target = isIme)
    val appBarAlpha by rememberDerivedStateOf {
        maxOf(1f - dragProgress, imeAnimated.visibleOnTargetAlpha(true))
    }
    val bottomButtonsAlpha by animateFloatAsState(
        if (scaffoldState.bottomSheetState.targetValue == SheetValue.Hidden) 0f else 1f,
        label = "bottom buttons alpha "
    )
    val siteVerticalRatio by rememberDerivedStateOf { (1f - dragProgress) * 0.1f }
    val backLauncher = rememberClickDebounced { router?.back() }

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures { scope.launch { scaffoldState.bottomSheetState.hide() } }
            },
    ) {
        BottomSheetBigDialog(
            sheetModifier = Modifier
                .pointerInput(Unit) { detectTapGestures { /* ignore */ } },
            sheetDragHandleModifier = Modifier
                .pointerInput(Unit) { detectTapGestures { /* ignore */ } },
            topMargin = AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding(),
            sheetPeekHeight = 460.dp,
            scaffoldState = scaffoldState,
            onDrag = { dragProgress = it },
            onClosed = backLauncher,
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                val (
                    issuerHintField, issuerField,
                    nameHintField, nameField,
                    codeHintField, codeField,
                ) = createRefs()

                Text(
                    modifier = Modifier
                        .alpha(0.6f)
                        .constrainAs(issuerHintField) {
                            linkTo(
                                top = parent.top,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = siteVerticalRatio,
                                horizontalBias = 0f,
                                startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                            )
                        },
                    style = theme.typeScheme.typography.labelMedium,
                    text = stringResource(id = CoreR.string.issuer),
                )

                TextButton(
                    modifier = Modifier
                        .padding(horizontal = safeContentPaddings.horizontal(16.dp))
                        .constrainAs(issuerField) {
                            linkTo(
                                top = issuerHintField.bottom,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = 0f,
                                horizontalBias = 0f,
                            )
                        },
                    onClick = rememberClickDebounced(presenter) { presenter?.copyIssuer(router) },
                    colors = theme.colorScheme.whiteTextButtonColors,
                ) {
                    Text(text = note.issuer)
                }

                Text(
                    modifier = Modifier
                        .alpha(0.6f)
                        .constrainAs(nameHintField) {
                            linkTo(
                                top = issuerField.bottom,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = 0f,
                                horizontalBias = 0f,
                                startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                                topMargin = 8.dp,
                            )
                        },
                    style = theme.typeScheme.typography.labelMedium,
                    text = stringResource(id = CoreR.string.name),
                )

                TextButton(
                    modifier = Modifier
                        .padding(horizontal = safeContentPaddings.horizontal(16.dp))
                        .constrainAs(nameField) {
                            linkTo(
                                top = nameHintField.bottom,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = 0f,
                                horizontalBias = 0f,
                            )
                        },
                    onClick = rememberClickDebounced(presenter) { presenter?.copyName(router) },
                    colors = theme.colorScheme.whiteTextButtonColors,
                ) {
                    Text(text = note.name)
                }


                Text(
                    modifier = Modifier
                        .alpha(0.6f)
                        .constrainAs(codeHintField) {
                            linkTo(
                                top = nameField.bottom,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = 0f,
                                horizontalBias = 0f,
                                startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                                topMargin = 8.dp,
                            )
                        },
                    style = theme.typeScheme.typography.labelMedium,
                    text = stringResource(id = CoreR.string.code),
                )

                TextButton(
                    modifier = Modifier
                        .padding(horizontal = safeContentPaddings.horizontal(16.dp))
                        .constrainAs(codeField) {
                            width = Dimension.preferredWrapContent
                            linkTo(
                                top = codeHintField.bottom,
                                bottom = parent.bottom,
                                start = parent.start,
                                end = parent.end,
                                verticalBias = 0f,
                                horizontalBias = 0f,
                            )
                        },
                    onClick = rememberClickDebounced(presenter) { presenter?.copyCode(router) },
                    colors = theme.colorScheme.whiteTextButtonColors,
                ) {
                    Text(text = note.otpPassw)
                }
            }
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .alpha(bottomButtonsAlpha)
            .fillMaxSize(),
    ) {
        val (editBtField) = createRefs()
        TextButton(
            modifier = Modifier.constrainAs(editBtField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
                    verticalBias = 1f,
                    horizontalBias = 1f,
                    startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    bottomMargin = safeContentPaddings.calculateBottomPadding() + 16.dp,
                )
            },
            onClick = rememberClickDebounced(presenter) { presenter?.edit(router) },
        ) {
            Text(text = stringResource(id = CoreR.string.edit))
        }
    }

    AppBarStates(
        modifier = Modifier
            .alpha(appBarAlpha),
        navigationIcon = {
            IconButton(
                onClick = backLauncher,
                content = { BackMenuIcon() }
            )
        },
        titleContent = { Text(text = stringResource(R.string.one_time_password)) },
    )
}


@OptIn(DebugOnly::class)
@Composable
@Preview(device = Devices.PHONE)
fun NotePreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun otpNotePresenter(noteIdentifier: NoteIdentifier) = object : OtpNotePresenter {
            override val note = MutableStateFlow(ColoredOtpNote.dummyLoaded())
        }
    })
    DebugDarkScreenPreview {
        Box(modifier = Modifier.background(Color.Yellow)) {
            OtpNoteDialog(
                initialValue = SheetValue.PartiallyExpanded,
            )
        }
    }
}

