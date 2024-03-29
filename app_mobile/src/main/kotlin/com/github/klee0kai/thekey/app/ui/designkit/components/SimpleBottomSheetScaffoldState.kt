@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.designkit.components

import android.os.Parcelable
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize


data class SimpleBottomSheetScaffoldState(
    val topContentSize: Dp = 190.dp,
    val appBarSize: Dp = 0.dp,
    val scaffoldState: BottomSheetScaffoldState,
) {

    companion object {

        fun saver(
            topContentSize: Dp = 190.dp,
            appBarSize: Dp = 0.dp,
            density: Density,
        ) = Saver<SimpleBottomSheetScaffoldState, SimpleBottomSheetScaffoldParcelable>(
            save = { state ->
                SimpleBottomSheetScaffoldParcelable(
                    sheetValue = state.scaffoldState.bottomSheetState.currentValue,

                    )
            },
            restore = { savedValue ->
                SimpleBottomSheetScaffoldState(
                    topContentSize = topContentSize,
                    appBarSize = appBarSize,
                    scaffoldState = BottomSheetScaffoldState(
                        bottomSheetState = SheetState(
                            skipPartiallyExpanded = false,
                            density = density,
                            initialValue = savedValue.sheetValue,
                            confirmValueChange = { true },
                            skipHiddenState = true
                        ),
                        snackbarHostState = SnackbarHostState(),
                    ),
                )
            }
        )

    }

}


@Parcelize
data class SimpleBottomSheetScaffoldParcelable(
    val sheetValue: SheetValue,
) : Parcelable

@Composable
@NonRestartableComposable
fun rememberSimpleBottomSheetScaffoldState(
    topContentSize: Dp = 190.dp,
    appBarSize: Dp = 0.dp,
): SimpleBottomSheetScaffoldState {
    val density = LocalDensity.current
    return rememberSaveable(
        topContentSize, appBarSize,
        saver = SimpleBottomSheetScaffoldState.saver(
            topContentSize = topContentSize,
            appBarSize = appBarSize,
            density = density,
        )
    ) {
        SimpleBottomSheetScaffoldState(
            topContentSize = topContentSize,
            appBarSize = appBarSize,
            scaffoldState = BottomSheetScaffoldState(
                bottomSheetState = SheetState(
                    skipPartiallyExpanded = false,
                    density = density,
                    initialValue = SheetValue.PartiallyExpanded,
                    confirmValueChange = { true },
                    skipHiddenState = true
                ),
                snackbarHostState = SnackbarHostState(),
            ),
        )
    }
}


fun simpleBottomSheetScaffoldState(
    density: Density,
    topContentSize: Dp = 190.dp,
    appBarSize: Dp = 0.dp,
): SimpleBottomSheetScaffoldState {
    return SimpleBottomSheetScaffoldState(
        scaffoldState = BottomSheetScaffoldState(
            bottomSheetState = SheetState(
                skipPartiallyExpanded = false,
                density = density,
                initialValue = SheetValue.PartiallyExpanded,
                confirmValueChange = { true },
                skipHiddenState = true
            ),
            snackbarHostState = SnackbarHostState(),
        ),
        appBarSize = appBarSize,
        topContentSize = topContentSize,
    )

}