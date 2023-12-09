package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults.filledIconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.ui.designkit.color.SurfaceScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold

@Preview
@Composable
fun StoragesScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val navigator = remember { DI.navigator() }
    val context = LocalView.current.context

    val storages = presenter.storages().collectAsState(initial = emptyList())

    SimpleBottomSheetScaffold(
        appBarSticky = {
            Text(text = stringResource(id = R.string.storages))
        },
        topContent = {
            val (groupsHint, groupsList) = createRefs()
            LazyRow(modifier = Modifier
                .wrapContentSize()
                .constrainAs(groupsList) {
                    linkTo(
                        top = parent.top,
                        start = parent.start,
                        bottom = parent.bottom,
                        end = parent.end,
                        verticalBias = 0.6f
                    )
                })
            {
                val list = (0..20).toList()
                list.forEachIndexed { index, i ->
                    val isFirst = index == 0
                    val isLast = list.lastIndex == index
                    item {
                        GroupCircle(
                            modifier = Modifier
                                .padding(
                                    start = if (isFirst) 16.dp else 4.dp,
                                    top = 16.dp,
                                    end = if (isLast) 16.dp else 4.dp,
                                    bottom = 16.dp
                                )
                        )
                    }
                }
            }
        },
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                storages.value.forEach { storage ->
                    item {
                        StorageItem(storage)
                    }
                }
            }
        },
    )
}


@Preview
@Composable
fun StorageItem(
    storage: Storage = Storage()
) {
    var storage = if (LocalView.current.isInEditMode) {
        Storage(path = "path", name = "name", description = "description")
    } else storage

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (colorGroup, path, desctiption) = createRefs()

        Divider(
            color = Color.Blue,
            modifier = Modifier
                .size(1.dp, 24.dp)
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 8.dp)
                    top.linkTo(parent.top, 2.dp)
                    bottom.linkTo(parent.bottom, 2.dp)
                }
        )


        Text(
            text = storage.path,
            modifier = Modifier
                .constrainAs(path) {
                    start.linkTo(colorGroup.end, 4.dp)
                }
        )
        Text(
            text = storage.description,
            modifier = Modifier
                .constrainAs(desctiption) {
                    start.linkTo(colorGroup.end, 4.dp)
                    top.linkTo(path.bottom)
                }
        )
    }
}


@Preview
@Composable
fun GroupCircle(
    name: String = "A",
    colorScheme: SurfaceScheme = SurfaceScheme(Color.Cyan, Color.White),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FilledIconButton(
        colors = filledIconButtonColors(
            containerColor = colorScheme.surfaceColor,
            contentColor = colorScheme.onSurfaceColor,
            disabledContainerColor = colorScheme.surfaceColor.copy(alpha = 0.4f),
            disabledContentColor = colorScheme.onSurfaceColor.copy(alpha = 0.4f),
        ),
        modifier = modifier
            .size(48.dp, 48.dp),
        shape = CircleShape,
        onClick = onClick
    ) {
        Text(text = name)
    }

}
