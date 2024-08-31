@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.github.klee0kai.thekey.app.ui.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.BottomSheetBigDialog
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.rememberSafeBottomSheetScaffoldState
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NoteScreen(
    dest: NoteDestination = NoteDestination(),
    initialValue: SheetValue = SheetValue.PartiallyExpanded,
) {
    val router by LocalRouter.currentRef
    val colorScheme = LocalColorScheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isIme = WindowInsets.isIme
    val presenter by rememberOnScreenRef {

    }
//    val storageItems by presenter!!.filteredItems.collectAsState(key = Unit, initial = null)

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
    val showStoragesTitle by rememberTargetCrossFaded { dragProgress > 0.1f }
    LaunchedEffect(Unit) {
        while (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
            delay(100.milliseconds)
        }
        router?.back()
    }

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) { detectTapGestures { router?.back() } },
    ) {
        BottomSheetBigDialog(
            sheetModifier = Modifier
                .pointerInput(Unit) { detectTapGestures { /* ignore */ } },
            sheetDragHandleModifier = Modifier
                .pointerInput(Unit) { detectTapGestures { /* ignore */ } },
            topMargin = AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding(),
            sheetPeekHeight = 400.dp,
            scaffoldState = scaffoldState,
            onDrag = { dragProgress = it }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = safeContentPaddings.calculateBottomPadding())
            ) {

            }
        }
    }

    AppBarStates(
        modifier = Modifier
            .alpha(
                maxOf(
                    1f - dragProgress,
                    imeAnimated.visibleOnTargetAlpha(true)
                )
            ),
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = { Text(text = stringResource(R.string.note)) },
    )
}


@OptIn(DebugOnly::class)
@Composable
@Preview(device = Devices.PHONE)
fun NotePreview() {
    DI.hardResetToPreview()
//    AutoFillDI.initAutoFillPresentersModule(object : AutofillPresentersModule() {
//        override fun autoFillSelectNotePresenter(storageIdentifier: StorageIdentifier) =
//            object : AutofillSelectNotePresenterDummy() {
//
//            }
//    })
    DebugDarkScreenPreview {
        Box(modifier = Modifier.background(Color.Yellow)) {
            NoteScreen()
        }
    }
}

