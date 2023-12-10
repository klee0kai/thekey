package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults.filledIconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.designkit.color.SurfaceScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween

private val TOP_CONTENT_SIZE = 190.dp


@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StoragesScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val navigator = remember { DI.navigator() }
    val context = LocalView.current.context
    val scaffoldState = rememberSimpleBottomSheetScaffoldState()

    val topContentAlpha = scaffoldState.dragProgress.floatValue
        .ratioBetween(0.3f, 0.7f)
        .coerceIn(0f, 1f)
        .accelerateDecelerate()

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        topContentSize = TOP_CONTENT_SIZE,
        appBarSticky = {
            Text(text = stringResource(id = R.string.storages))
        },
        topContent = {
            GroupsSelectContainer(
                modifier = Modifier
                    .fillMaxHeight()
                    .alpha(topContentAlpha)
            )
        },
        sheetContent = {
            StoragesListContent(
                showStoragesTitle = scaffoldState.dragProgress.floatValue > 0.1f,
                modifier = Modifier.fillMaxSize()
            )
        },
    )
}

@Preview
@Composable
fun GroupsSelectContainer(
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (groupsHint, groupsList, indicator) = createRefs()

        Text(
            text = stringResource(id = R.string.groups),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.constrainAs(groupsHint) {
                linkTo(
                    start = parent.start,
                    top = parent.top,
                    bottom = groupsList.top,
                    end = parent.end,
                    horizontalBias = 0f,
                    startMargin = 16.dp,
                    verticalBias = 1f,
                )
            }
        )

        LazyListIndicatorIfNeed(
            lazyListState = lazyListState,
            horizontal = true,
            modifier = Modifier
                .size(52.dp, 4.dp)
                .constrainAs(indicator) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsList.bottom,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                    )
                },
        )

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
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
    }
}


@Preview
@Composable
fun StoragesListContent(
    showStoragesTitle: Boolean = true,
    modifier: Modifier = Modifier
        .height(600.dp)
        .background(Color.DarkGray),
) {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val storages = presenter.storages().collectAsState(initial = emptyList())
    val titleAnimatedAlpha by animateFloatAsState(
        targetValue = if (showStoragesTitle) 1.0f else 0f,
        label = "title animate"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(id = R.string.storages),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .alpha(titleAnimatedAlpha)
            )
        }

        storages.value.forEach { storage ->
            item(
                key = storage.path,
                contentType = storage::class,
            ) {
                StorageItem(storage)
            }
        }
    }
}


@Preview
@Composable
fun StorageItem(
    storage: ColoredStorage = ColoredStorage()
) {
    val colorScheme = remember { DI.theme().colorScheme() }
    val storage = if (LocalView.current.isInEditMode) {
        ColoredStorage(path = "path", name = "name", description = "description")
    } else storage

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (colorGroup, path, description) = createRefs()

        Box(
            modifier = Modifier
                .size(1.dp, 24.dp)
                .background(colorScheme.surfaceScheme(storage.colorGroup).surfaceColor)
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(path.top, 4.dp)
                }
        )

        Text(
            text = storage.path,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(path) {
                    linkTo(
                        start = colorGroup.end,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                }
        )
        Text(
            text = storage.description,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .constrainAs(description) {
                    linkTo(
                        start = colorGroup.end,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(path.bottom, margin = 4.dp)
                }
        )
    }
}


@Preview
@Composable
fun GroupCircle(
    modifier: Modifier = Modifier,
    name: String = "A",
    colorScheme: SurfaceScheme = SurfaceScheme(Color.Cyan, Color.White),
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
