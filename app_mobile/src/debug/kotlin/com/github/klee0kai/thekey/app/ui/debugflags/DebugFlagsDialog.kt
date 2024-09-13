@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.debugflags

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.domain.model.next
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.BottomSheetBigDialog
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.rememberSafeBottomSheetScaffoldState
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.StatusPreference
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun DebugFlagsDialog(
    initialValue: SheetValue = SheetValue.Hidden,
) {
    val router by LocalRouter.currentRef
    val colorScheme = LocalColorScheme.current
    val theme = LocalTheme.current
    val scope = rememberCoroutineScope()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isIme = WindowInsets.isIme
    val scaffoldState = rememberSafeBottomSheetScaffoldState(
        initialValue = initialValue,
        skipHiddenState = false,
    )
    val scrollState = rememberLazyListState()
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
    val backLauncher = rememberClickDebounced { router?.back() }

    var fastUpdateState by remember { mutableStateOf(DebugConfigs.isNotesFastUpdate) }
    var engineDelayState by remember { mutableStateOf(DebugConfigs.engineDelay) }

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
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                item("fast update") {
                    StatusPreference(
                        text = "fast update",
                        status = "$fastUpdateState",
                        onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                            fastUpdateState = !fastUpdateState
                            DebugConfigs.isNotesFastUpdate = fastUpdateState
                        },
                    )
                }

                item("engine delay") {
                    StatusPreference(
                        text = "Engine delay",
                        status = engineDelayState.name,
                        onClick = rememberClickDebounced(debounce = 100.milliseconds) {
                            engineDelayState = engineDelayState.next()
                            DebugConfigs.engineDelay = engineDelayState
                        },
                    )
                }
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
        titleContent = { Text(text = stringResource(CoreR.string.debug_flags)) },
    )
}


@OptIn(DebugOnly::class)
@Composable
@Preview(device = Devices.PHONE)
fun DebugFlagsDialogPreview() {
    DebugDarkScreenPreview {
        Box(modifier = Modifier.background(Color.Yellow)) {
            DebugFlagsDialog(
                initialValue = SheetValue.PartiallyExpanded,
            )
        }
    }
}

