@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.di.hardResetToPreview
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.BottomSheetBigDialog
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.rememberSafeBottomSheetScaffoldState
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.core.ui.navigation.model.SimpleDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.createDialogBottomAnchor
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.launch

@Composable
fun SimpleDialog(
    dest: SimpleDialogDestination = SimpleDialogDestination(),
    initialValue: SheetValue = SheetValue.Hidden,
) {
    val router by LocalRouter.currentRef
    val colorScheme = LocalColorScheme.current
    val theme = LocalTheme.current
    val resources = LocalContext.current.resources
    val scope = rememberCoroutineScope()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isIme = WindowInsets.isIme

    val scaffoldState = rememberSafeBottomSheetScaffoldState(
        initialValue = initialValue,
        skipHiddenState = false,
    )
    val isMessageLongText = remember(dest) { dest.message.text(resources).length > 200 }
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
    val backLauncher = rememberClickDebounced {
        router?.backWithResult(ConfirmDialogResult.CANCELED)
    }
    val sheetPeekHeight = 400.dp

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
            sheetPeekHeight = sheetPeekHeight,
            scaffoldState = scaffoldState,
            onDrag = { dragProgress = it },
            onClosed = backLauncher,
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val (titleField, messageField) = createRefs()
                val dialogBottom = createDialogBottomAnchor(
                    sheetPeekHeight = sheetPeekHeight,
                    dragProcess = dragProgress,
                    bottomMargin = safeContentPaddings.calculateBottomPadding(),
                )

                Text(
                    modifier = Modifier
                        .alpha(1f - appBarAlpha)
                        .constrainAs(titleField) {
                            width = Dimension.fillToConstraints
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = dialogBottom.bottom,
                                bottomMargin = safeContentPaddings.calculateBottomPadding(),
                                verticalBias = 0f,
                                horizontalBias = 0f,
                                startMargin = safeContentPaddings.horizontal(16.dp),
                                endMargin = safeContentPaddings.horizontal(16.dp),
                            )
                        },
                    text = dest.title.text(resources),
                    style = theme.typeScheme.screenHeader,
                )


                Text(
                    modifier = Modifier
                        .constrainAs(messageField) {
                            width = Dimension.fillToConstraints
                            if (isMessageLongText) {
                                height = Dimension.fillToConstraints
                            }
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = titleField.bottom,
                                bottom = dialogBottom.bottom,
                                topMargin = 16.dp,
                                bottomMargin = safeContentPaddings.calculateBottomPadding(),
                                startMargin = safeContentPaddings.horizontal(16.dp),
                                endMargin = safeContentPaddings.horizontal(16.dp),
                            )
                        },
                    text = dest.message.text(resources),
                    textAlign = if (isMessageLongText) TextAlign.Start else TextAlign.Center,
                    style = theme.typeScheme.body,
                )

            }
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .alpha(bottomButtonsAlpha)
            .fillMaxSize(),
    ) {
        val (shadowField, confirmBtField, rejectBtField) = createRefs()

        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to colorScheme.androidColorScheme.surface,
                        1f to colorScheme.androidColorScheme.surface,
                    )
                )
                .constrainAs(shadowField) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    linkTo(
                        top = confirmBtField.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 1f,
                        topMargin = (-64).dp
                    )
                }
        )

        TextButton(
            modifier = Modifier.constrainAs(confirmBtField) {
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
            onClick = rememberClickDebounced {
                router?.backWithResult(ConfirmDialogResult.CONFIRMED)
            },
        ) {
            Text(
                text = dest.confirm.text(resources),
                style = theme.typeScheme.buttonText,
            )
        }

        if (dest.reject != null) {
            TextButton(
                modifier = Modifier.constrainAs(rejectBtField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = confirmBtField.start,
                        verticalBias = 1f,
                        horizontalBias = 1f,
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                        endMargin = 16.dp,
                        bottomMargin = safeContentPaddings.calculateBottomPadding() + 16.dp,
                    )
                },
                onClick = rememberClickDebounced {
                    router?.backWithResult(ConfirmDialogResult.REJECTED)
                },
            ) {
                Text(
                    text = dest.reject.text(resources),
                    style = theme.typeScheme.buttonText,
                )
            }
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
        titleContent = { Text(text = dest.title.text(resources)) },
    )
}


@OptIn(DebugOnly::class)
@Composable
@Preview(device = Devices.PHONE)
fun SimpleDialogPreview() {
    CoreDI.hardResetToPreview()
    DebugDarkScreenPreview {
        Box(modifier = Modifier.background(Color.Yellow)) {
            SimpleDialog(
                initialValue = SheetValue.Expanded,
                dest = SimpleDialogDestination(
                    title = TextProvider(R.string.title),
                    message = TextProvider(LoremIpsum.getInstance().getWords(2)),
                    confirm = TextProvider(R.string.ok),
                    reject = TextProvider(R.string.cancel),
                )
            )
        }
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview(device = Devices.PHONE)
fun SimpleDialogLongPreview() {
    CoreDI.hardResetToPreview()
    DebugDarkScreenPreview {
        Box(modifier = Modifier.background(Color.Yellow)) {
            SimpleDialog(
                initialValue = SheetValue.Expanded,
                dest = SimpleDialogDestination(
                    title = TextProvider(R.string.title),
                    message = TextProvider(LoremIpsum.getInstance().getWords(120)),
                    confirm = TextProvider(R.string.ok),
                    reject = TextProvider(R.string.cancel),
                )
            )
        }
    }
}



