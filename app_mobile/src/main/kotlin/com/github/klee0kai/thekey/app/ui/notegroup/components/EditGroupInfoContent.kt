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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.designkit.color.transparentColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.ui.designkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.app.ui.designkit.components.scrollPosition

@Composable
fun EditGroupInfoContent(
    modifier: Modifier = Modifier,
    groupNameFieldModifier: Modifier = Modifier,
    groupName: String = "",
    select: KeyColor = KeyColor.NOCOLOR,
    forceIndicatorVisible: Boolean = false,
    onChangeGroupName: (String) -> Unit = {},
    onSelect: (KeyColor) -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current
    val lazyListState = rememberLazyListState()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (groupsHint, groupsList, indicator, groupNameField) = createRefs()

        Text(
            text = stringResource(id = R.string.select_color),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.constrainAs(groupsHint) {
                linkTo(
                    start = parent.start,
                    top = parent.top,
                    bottom = groupsList.top,
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
                        top = groupsHint.bottom,
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
                        colorScheme = colorScheme.surfaceScheme(color),
                        onClick = { onSelect(color) },
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = groupNameFieldModifier
                .wrapContentHeight()
                .width(224.dp)
                .constrainAs(groupNameField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsList.bottom,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        startMargin = 16.dp,
                        topMargin = 8.dp
                    )
                },

            label = { Text(modifier = Modifier, text = stringResource(R.string.group_symbol)) },
            colors = TextFieldDefaults.transparentColorScheme(),
            value = groupName,
            onValueChange = onChangeGroupName
        )

    }
}


@Preview
@Composable
private fun EditGroupInfoContentPreview() = AppTheme {
    EditGroupInfoContent(
        forceIndicatorVisible = true,
    )
}

@Preview
@Composable
private fun EditGroupInfoContentInBoxPreview() = AppTheme {
    Box(modifier = Modifier.fillMaxSize()) {
        EditGroupInfoContent(
            modifier = Modifier.align(Alignment.Center),
            forceIndicatorVisible = true,
        )
    }
}
