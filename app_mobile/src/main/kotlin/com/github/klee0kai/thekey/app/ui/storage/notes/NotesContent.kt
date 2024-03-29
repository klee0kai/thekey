@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.simpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

@Preview
@Composable
fun NotesContent(
    modifier: Modifier = Modifier,
    args: StorageDestination = StorageDestination(),
    isPageFullyAvailable: Boolean = false,
    scaffoldState: SimpleBottomSheetScaffoldState = simpleBottomSheetScaffoldState(LocalDensity.current),
    onDrag: (Float) -> Unit = {},
) {
    val router = LocalRouter.current
    val dragProgress = remember { mutableFloatStateOf(0f) }
    val addButtonAlpha by animateAlphaAsState(isPageFullyAvailable)
    val addButtonVisible by rememberDerivedStateOf { addButtonAlpha > 0 }
    val showStoragesTitle by rememberDerivedStateOf { dragProgress.floatValue > 0.1f }

    SimpleBottomSheetScaffold(
        modifier = modifier,
        simpleBottomSheetScaffoldState = scaffoldState,
        onDrag = {
            dragProgress.floatValue = it
            onDrag.invoke(it)
        },
        topContent = {
            GroupsSelectContent(
                dragProgress = dragProgress,
            )
        },
        sheetContent = {
            NotesListContent(
                modifier = Modifier.fillMaxSize(),
                args = args,
                showStoragesTitle = showStoragesTitle,
            )
        },
    )

    if (addButtonVisible) {
        FabSimpleInContainer(
            modifier = Modifier.alpha(addButtonAlpha),
            onClick = remember { { router.navigate(args.note()) } },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }

}