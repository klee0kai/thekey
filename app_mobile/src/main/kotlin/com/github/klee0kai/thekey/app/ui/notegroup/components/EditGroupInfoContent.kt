package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.scrollPosition
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreference
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.utils.views.transparentColors
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun EditGroupInfoContent(
    modifier: Modifier = Modifier,
    groupNameFieldModifier: Modifier = Modifier,
    groupName: String = "",
    select: KeyColor = KeyColor.NOCOLOR,
    forceIndicatorVisible: Boolean = false,
    favoriteVisible: Boolean = false,
    favoriteChecked: Boolean = false,
    onChangeGroupName: (String) -> Unit = {},
    onSelect: (KeyColor) -> Unit = {},
    onFavoriteChecked: (Boolean) -> Unit = {},
) {
    val theme = LocalTheme.current
    val lazyListState = rememberLazyListState()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (
            groupsHintField,
            groupsListField,
            indicatorField,
            groupNameField,
            favoriteSwitchField,
        ) = createRefs()

        Text(
            text = stringResource(id = R.string.select_color),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.constrainAs(groupsHintField) {
                linkTo(
                    start = parent.start,
                    top = parent.top,
                    bottom = groupsListField.top,
                    end = parent.end,
                    horizontalBias = 0f,
                    topMargin = 16.dp,
                    startMargin = 16.dp,
                    verticalBias = 1f,
                )
            }
        )

        LazyListIndicatorIfNeed(
            pos = lazyListState.scrollPosition(),
            forceVisible = forceIndicatorVisible,
            horizontal = true,
            modifier = Modifier
                .size(52.dp, 4.dp)
                .constrainAs(indicatorField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsListField.bottom,
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
                .constrainAs(groupsListField) {
                    linkTo(
                        top = groupsHintField.bottom,
                        start = parent.start,
                        bottom = groupNameField.top,
                        end = parent.end,
                        verticalBias = 0f
                    )
                })
        {
            item {
                Spacer(modifier = Modifier.width(14.dp))
            }

            KeyColor.colors.forEachIndexed { index, color ->
                item(key = color) {
                    GroupCircle(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(
                                start = 1.dp,
                                top = 8.dp,
                                end = 1.dp,
                                bottom = 8.dp
                            ),
                        buttonSize = 56.dp,
                        checked = color == select,
                        colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(color),
                        onClick = { onSelect(color) },
                    )
                }
            }
        }

        AppTextField(
            modifier = groupNameFieldModifier
                .wrapContentHeight()
                .width(224.dp)
                .constrainAs(groupNameField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsListField.bottom,
                        bottom = if (favoriteVisible) favoriteSwitchField.top else parent.bottom,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        startMargin = 16.dp,
                        topMargin = 8.dp
                    )
                },

            label = { Text(modifier = Modifier, text = stringResource(R.string.group_symbol)) },
            value = groupName,
            onValueChange = onChangeGroupName,
            colors = TextFieldDefaults.transparentColors(),
        )

        if (favoriteVisible) {
            SwitchPreference(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(favoriteSwitchField) {
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            top = groupNameField.bottom,
                            bottom = parent.bottom,
                            verticalBias = 0f,
                            topMargin = 8.dp
                        )
                    },
                text = stringResource(id = R.string.favorite),
                checked = favoriteChecked,
                onCheckedChange = onFavoriteChecked,
            )
        }

    }
}

@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentPreview() = AppTheme {
    EditGroupInfoContent(
        forceIndicatorVisible = true,
    )
}

@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentFavoritePreview() = AppTheme {
    EditGroupInfoContent(
        forceIndicatorVisible = true,
        favoriteVisible = true,
    )
}

@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentInBoxPreview() = AppTheme {
    Box(modifier = Modifier.fillMaxSize()) {
        EditGroupInfoContent(
            modifier = Modifier.align(Alignment.Center),
            forceIndicatorVisible = true,
        )
    }
}
