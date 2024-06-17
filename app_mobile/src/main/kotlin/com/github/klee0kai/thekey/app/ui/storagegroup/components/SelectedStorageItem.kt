package com.github.klee0kai.thekey.app.ui.storagegroup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.ui.storagegroup.model.SelectedStorage
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SelectedStorageItem(
    modifier: Modifier = Modifier,
    storage: SelectedStorage = SelectedStorage(),
    onSelected: ((Boolean) -> Unit)? = null,
    overlayContent: @Composable () -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current
    val animatedStorage by animateTargetCrossFaded(storage)
    val icon by animateTargetCrossFaded(target = if (storage.selected) Icons.Default.Check else Icons.Filled.Add)

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .fillMaxWidth()
            .run {
                if (onSelected != null) {
                    clickable { onSelected(!storage.selected) }
                } else {
                    this
                }
            }
    ) {
        val (
            colorGroupField, siteField, loginField, descriptionField,
            addIconField,
        ) = createRefs()


        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceSchemas.surfaceScheme(storage.group.keyColor).surfaceColor,
                    shape = RoundedCornerShape(2.dp),
                )
                .constrainAs(colorGroupField) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        bottom = parent.bottom,
                        end = parent.end,
                        verticalBias = 0.5f,
                        horizontalBias = 0f,
                        startMargin = 16.dp,
                    )
                }
        )

        Text(
            text = animatedStorage.current.path,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .alpha(animatedStorage.alpha)
                .constrainAs(siteField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroupField.end,
                        end = loginField.start,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 8.dp,
                        horizontalBias = 0f,
                        verticalBias = 0f,
                    )
                }
        )

        Text(
            text = animatedStorage.current.name,
            style = MaterialTheme.typography.bodyMedium
                .copy(color = LocalColorScheme.current.androidColorScheme.primary),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .alpha(animatedStorage.alpha)
                .constrainAs(loginField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = siteField.end,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 8.dp,
                        endMargin = 26.dp,
                        horizontalBias = 0.6f,
                        verticalBias = 0f,
                    )
                }
        )

        Text(
            text = animatedStorage.current.description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .alpha(animatedStorage.alpha)
                .constrainAs(descriptionField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = siteField.bottom,
                        bottom = parent.bottom,
                        start = colorGroupField.end,
                        end = siteField.end,
                        topMargin = 4.dp,
                        startMargin = 16.dp,
                        bottomMargin = 6.dp,
                        horizontalBias = 0f,
                        verticalBias = 1f,
                    )
                }
        )

        Icon(
            modifier = Modifier
                .alpha(icon.alpha)
                .alpha(animatedStorage.alpha)
                .constrainAs(addIconField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        endMargin = 16.dp,
                        horizontalBias = 1f,
                        verticalBias = 0.5f,
                    )
                },
            imageVector = icon.current,
            contentDescription = "Added"
        )

        overlayContent()
    }
}


@VisibleForTesting
@Composable
@Preview
fun SelectStorageDummyPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    SelectedStorageItem(
        storage = SelectedStorage(
            path = "somePath1",
            name = "work",
            description = "work passwords",
            group = ColorGroup(
                name = "CO",
                keyColor = KeyColor.CORAL,
            )
        )
    )
}

@VisibleForTesting
@Composable
@Preview
fun SelectStorageDummyNoGroupPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    SelectedStorageItem(
        storage = SelectedStorage(
            path = "somePath1",
            name = "work",
            description = "work passwords",
            group = ColorGroup()
        )
    )
}