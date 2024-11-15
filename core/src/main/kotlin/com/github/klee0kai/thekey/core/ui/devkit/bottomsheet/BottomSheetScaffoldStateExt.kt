@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.bottomsheet

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
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

object SimpleScaffoldConst {
    val dragHandleSize = 48.dp
}

object BottomSheetScaffoldStateExt {

    fun saver(
        density: Density,
        skipPartiallyExpanded: Boolean = false,
        confirmValueChange: (SheetValue) -> Boolean = { true },
        skipHiddenState: Boolean = true,
    ) = Saver<BottomSheetScaffoldState, SimpleBottomSheetScaffoldParcelable>(
        save = { state ->
            SimpleBottomSheetScaffoldParcelable(
                sheetValue = state.bottomSheetState.currentValue,

                )
        },
        restore = { savedValue ->
            BottomSheetScaffoldState(
                bottomSheetState = SheetState(
                    skipPartiallyExpanded = skipPartiallyExpanded,
                    density = density,
                    initialValue = savedValue.sheetValue,
                    confirmValueChange = confirmValueChange,
                    skipHiddenState = skipHiddenState
                ),
                snackbarHostState = SnackbarHostState(),
            )
        }
    )

}

@Parcelize
data class SimpleBottomSheetScaffoldParcelable(
    val sheetValue: SheetValue,
) : Parcelable

@Composable
@NonRestartableComposable
fun rememberSafeBottomSheetScaffoldState(
    initialValue: SheetValue = SheetValue.PartiallyExpanded,
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    skipHiddenState: Boolean = true,
): BottomSheetScaffoldState {
    val density = LocalDensity.current
    return rememberSaveable(
        saver = BottomSheetScaffoldStateExt.saver(
            density = density,
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = confirmValueChange,
            skipHiddenState = skipHiddenState,
        )
    ) {
        BottomSheetScaffoldState(
            bottomSheetState = SheetState(
                skipPartiallyExpanded = skipPartiallyExpanded,
                density = density,
                initialValue = initialValue,
                confirmValueChange = confirmValueChange,
                skipHiddenState = skipHiddenState,
            ),
            snackbarHostState = SnackbarHostState(),
        )
    }
}