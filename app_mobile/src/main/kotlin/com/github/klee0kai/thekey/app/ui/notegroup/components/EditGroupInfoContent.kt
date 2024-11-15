package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.externalStorages
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.scrollPosition
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreference
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.rememberClick
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.thenIf
import com.github.klee0kai.thekey.core.utils.views.transparentColors
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import kotlinx.coroutines.delay
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration.Companion.seconds

@Composable
fun EditGroupInfoContent(
    modifier: Modifier = Modifier,
    groupNameFieldModifier: Modifier = Modifier,
    groupName: String = "",
    variants: List<ColorGroup> = emptyList(),
    selectedId: Long = -1,
    forceIndicatorVisible: Boolean = false,
    favoriteVisible: Boolean = false,
    favoriteChecked: Boolean = false,
    onChangeGroupName: (String) -> Unit = {},
    onSelect: (ColorGroup) -> Unit = {},
    onFavoriteChecked: (Boolean) -> Unit = {},
) {
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val lazyListState = rememberLazyListState()
    val scrollPosition by rememberDerivedStateOf { lazyListState.scrollPosition() }

    val favoriteVisibleAnimated by animateTargetFaded(favoriteVisible)

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
            style = theme.typeScheme.header,
            color = theme.colorScheme.textColors.hintTextColor,
            modifier = Modifier.constrainAs(groupsHintField) {
                linkToParent(
                    bottom = groupsListField.top,
                    horizontalBias = 0f,
                    topMargin = 16.dp,
                    startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    verticalBias = 1f,
                )
            }
        )

        LazyListIndicatorIfNeed(
            pos = scrollPosition,
            forceVisible = forceIndicatorVisible,
            horizontal = true,
            modifier = Modifier
                .size(52.dp, 4.dp)
                .constrainAs(indicatorField) {
                    linkToParent(
                        top = groupsListField.bottom,
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
                    linkToParent(
                        top = groupsHintField.bottom,
                        bottom = groupNameField.top,
                        verticalBias = 0f,
                    )
                })
        {
            item("start_spacer") {
                Spacer(
                    modifier = Modifier.width(
                        safeContentPaddings.horizontal(minValue = 16.dp) - 4.dp
                    )
                )
            }

            variants.forEachIndexed { _, group ->
                item(key = group.id) {
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
                        checked = group.id == selectedId,
                        name = group.name,
                        colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(group.keyColor),
                        onClick = rememberClick(group) { onSelect(group) },
                    )
                }
            }

            item("end_spacer") {
                Spacer(
                    modifier = Modifier.width(
                        safeContentPaddings.horizontal(minValue = 16.dp) - 4.dp
                    )
                )
            }
        }

        AppTextField(
            modifier = groupNameFieldModifier
                .wrapContentHeight()
                .constrainAs(groupNameField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsListField.bottom,
                        bottom = favoriteSwitchField.top,
                        topMargin = 8.dp
                    )
                },

            label = { Text(modifier = Modifier, text = stringResource(R.string.group_symbol)) },
            value = groupName,
            onValueChange = onChangeGroupName,
            colors = TextFieldDefaults.transparentColors(),
        )

        SwitchPreference(
            modifier = Modifier
                .animateContentSize()
                .wrapContentHeight()
                .thenIf(!favoriteVisibleAnimated.current) { height(0.dp) }
                .alpha(favoriteVisibleAnimated.visibleOnTargetAlpha(true))
                .constrainAs(favoriteSwitchField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupNameField.bottom,
                        bottom = parent.bottom,
                    )
                }
                .clickable(onClick = { onFavoriteChecked(!favoriteChecked) })
                .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                .padding(vertical = 12.dp),
            text = stringResource(id = R.string.favorite),
            checked = favoriteChecked,
        )

    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentPreview() = DebugDarkContentPreview {
    EditGroupInfoContent(
        variants = KeyColor.selectableColorGroups,
        forceIndicatorVisible = true,
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentFavoritePreview() = DebugDarkContentPreview {
    var favoriteVisible by remember { mutableStateOf(true) }
    var favoriteChecked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            favoriteVisible = !favoriteVisible
        }
    }
    EditGroupInfoContent(
        variants = KeyColor.selectableColorGroups,
        forceIndicatorVisible = true,
        favoriteVisible = favoriteVisible,
        favoriteChecked = favoriteChecked,
        onFavoriteChecked = { favoriteChecked = !favoriteChecked }
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun EditGroupInfoContentInBoxPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.fillMaxSize()) {
        EditGroupInfoContent(
            variants = listOf(ColorGroup.externalStorages()) + KeyColor.selectableColorGroups,
            modifier = Modifier.align(Alignment.Center),
            forceIndicatorVisible = true,
        )
    }
}
