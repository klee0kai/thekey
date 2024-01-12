@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.navigation.NoteDestination
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.app.utils.common.animateAlphaAsState
import dev.olshevski.navigation.reimagined.navigate

@Preview
@Composable
fun NotesContent(
    modifier: Modifier = Modifier,
    storagePath: String = "",
    isPageFullyAvailable: Boolean = false,
    scaffoldState: SimpleBottomSheetScaffoldState =
        rememberSimpleBottomSheetScaffoldState(
            topContentSize = 190.dp,
            appBarSize = AppBarConst.appBarSize
        )
) {
    val navigator = remember { DI.navigator() }
    val addButtonAlpha by animateAlphaAsState(isPageFullyAvailable)

    SimpleBottomSheetScaffold(
        modifier = modifier
            .padding(top = SecondaryTabsConst.allHeight),
        simpleBottomSheetScaffoldState = scaffoldState,
        topContent = {
            GroupsSelectContent(
                scaffoldState = scaffoldState,
            )
        },
        sheetContent = {
            NotesListContent(
                modifier = Modifier.fillMaxSize(),
                storagePath = storagePath,
                showStoragesTitle = scaffoldState.dragProgress.floatValue > 0.1f,
            )
        },
    )

    if (addButtonAlpha > 0) {
        FabSimpleInContainer(
            modifier = Modifier.alpha(addButtonAlpha),
            onClick = { navigator.navigate(NoteDestination(path = storagePath)) },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }

}