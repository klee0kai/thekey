package com.github.klee0kai.thekey.app.ui.simpleboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString
import com.thedeanda.lorem.LoremIpsum
import com.github.klee0kai.thekey.app.R as AppR

@Composable
fun CurrentStorageHeader(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    val pathInputHelper = remember { DI.pathInputHelper() }
    val isDescNotEmpty = storage.description.isNotBlank() || storage.name.isNotBlank()
    val pathShortPath = with(pathInputHelper) {
        storage.path
            .shortPath()
            .toAnnotationString()
            .coloredPath(accentColor = colorScheme.androidColorScheme.primary)
            .coloredFileExt(extensionColor = theme.colorScheme.textColors.hintTextColor)
    }

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minWidth = 200.dp)
    ) {
        val (titleHeader, iconField, storagePathField, storageNameField) = createRefs()

        Text(
            text = stringResource(id = R.string.current_storage),
            modifier = Modifier
                .constrainAs(titleHeader) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 24.dp,
                        startMargin = safeContentPadding.horizontal(minValue = 16.dp),
                        endMargin = 16.dp,
                    )
                },
            style = theme.typeScheme.screenHeader,
        )


        Image(
            modifier = Modifier
                .size(48.dp, 48.dp)
                .constrainAs(iconField) {
                    linkToParent(
                        top = titleHeader.bottom,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 28.dp,
                        startMargin = 4.dp,
                        bottomMargin = 28.dp,
                    )
                },
            painter = painterResource(id = AppR.drawable.key_to_left),
            contentDescription = "key",
        )

        Text(
            modifier = Modifier
                .constrainAs(storagePathField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        top = iconField.top,
                        bottom = iconField.bottom,
                        start = iconField.end,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                    )
                },
            style = theme.typeScheme.body,
            text = pathShortPath,
        )

        if (isDescNotEmpty) {
            Text(
                text = when {
                    storage.name.isNotBlank() && storage.description.isNotBlank() -> "${storage.name}  ~  ${storage.description}"
                    else -> "${storage.name}${storage.description}"
                },
                style = theme.typeScheme.bodySmall,
                color = theme.colorScheme.textColors.hintTextColor,
                modifier = Modifier
                    .constrainAs(storageNameField) {
                        width = Dimension.fillToConstraints
                        linkToParent(
                            top = storagePathField.bottom,
                            bottom = iconField.bottom,
                            start = iconField.end,
                            topMargin = 8.dp,
                            startMargin = 8.dp,
                            endMargin = 16.dp,
                            verticalBias = 0f,
                            horizontalBias = 0f,
                            bottomMargin = 24.dp,
                        )
                    },
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Preview(widthDp = 400)
@Composable
fun CurrentStorageHeaderPreview() {
    DI.hardResetToPreview()
    DebugDarkContentPreview {
        CurrentStorageHeader(
            storage = ColoredStorage(
                path = "/phoneStorage/Documents/pet.ckey",
                name = LoremIpsum().city,
                description = LoremIpsum().getWords(6)
            )
        )
    }
}

@OptIn(DebugOnly::class)
@Preview(widthDp = 400)
@Composable
fun CurrentStorageHeaderNoDescPreview() {
    DI.hardResetToPreview()
    DebugDarkContentPreview {
        CurrentStorageHeader(
            storage = ColoredStorage(
                path = "/phoneStorage/Documents/pet.ckey",
            )
        )
    }
}