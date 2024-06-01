@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.BottomSheetBigDialog
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.rememberSafeBottomSheetScaffoldState
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreen
import com.github.klee0kai.thekey.core.utils.views.topDp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.delay
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SelectStorageDialog() = Box(modifier = Modifier.fillMaxSize()) {
    val router = LocalRouter.current
    val presenter = rememberOnScreen { DI.storagesPresenter().apply { init() } }
    val scaffoldState = rememberSafeBottomSheetScaffoldState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = false,
    )
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val showStoragesTitle by rememberDerivedStateOf { dragProgress > 0.1f }

    LaunchedEffect(key1 = Unit) {
        while (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
            delay(100)
        }
        router.back()
    }
    Box(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = ((1f - dragProgress) + 0.4f).coerceIn(0f, 1f)))
            .pointerInput(Unit) { router.back() }
    ) {
        BottomSheetBigDialog(
            topMargin = AppBarConst.appBarSize + WindowInsets.safeContent.topDp,
            scaffoldState = scaffoldState,
            onDrag = { dragProgress = it }
        ) {
            StoragesListContent(
                modifier = Modifier
                    .fillMaxSize(),
                showStoragesTitle = showStoragesTitle,
            )
        }


        AppBarStates(
            modifier = Modifier.alpha(1f - dragProgress),
            navigationIcon = {
                IconButton(onClick = remember { { router.back() } }) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            titleContent = { Text(text = stringResource(id = R.string.storages)) },
        )
    }

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview(device = Devices.PHONE)
fun SelectStorageDialogPreview() = EdgeToEdgeTemplate {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagesPresenter() = StoragesPresenterDummy()
    })
    AppTheme {
        Box(modifier = Modifier.background(Color.Yellow)) {
            SelectStorageDialog()
        }
    }
}