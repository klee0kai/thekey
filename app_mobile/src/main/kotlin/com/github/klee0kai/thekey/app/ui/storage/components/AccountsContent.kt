@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.navigation.Destination
import dev.olshevski.navigation.reimagined.navigate

@Preview
@Composable
fun AccountsContent(
    modifier: Modifier = Modifier,
    isPageFullyAvailable: Boolean = false,
    scaffoldState: SimpleBottomSheetScaffoldState =
        rememberSimpleBottomSheetScaffoldState(
            topContentSize = 190.dp,
            appBarSize = AppBarConst.appBarSize
        )
) {
    val navigator = remember { DI.navigator() }

    val addButtonAlpha by animateFloatAsState(
        targetValue = if (isPageFullyAvailable) 1f else 0f,
        label = "addButton visible animate"
    )

    SimpleBottomSheetScaffold(
        modifier = modifier,
        simpleBottomSheetScaffoldState = scaffoldState,
        topContent = {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        },
        sheetContent = {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        },
    )

    if (addButtonAlpha > 0) {
        FabSimpleInContainer(
            modifier = Modifier.alpha(addButtonAlpha),
            onClick = { navigator.navigate(Destination.AccountScreen()) },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }

}