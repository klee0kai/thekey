package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.designkit.color.transparentColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.ui.storages.components.GroupCircle

@Composable
@Preview
fun EditGroupInfoContent(
    modifier: Modifier = Modifier,
    groupNameFieldModifier: Modifier = Modifier,
    groupName: String = "",
    select: KeyColor = KeyColor.NOCOLOR,
    onChangeGroupName: (String) -> Unit = {},
    onSelect: (KeyColor) -> Unit = {},
) {
    val colorScheme = remember { DI.theme().colorScheme() }
    val lazyListState = rememberLazyListState()


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
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
            KeyColor.colors.forEachIndexed { index, color ->
                item(key = color) {
                    val isFirst = index == 0

                    val scaleAnimated by animateFloatAsState(if (color == select) 1f else 0.7f, label = "color group scale")

                    GroupCircle(
                        name = "",
                        colorScheme = colorScheme.surfaceScheme(color),
                        modifier = Modifier
                            .animateContentSize()
                            .scale(scaleAnimated)
                            .padding(
                                start = if (isFirst) 16.dp else 4.dp,
                                top = 16.dp,
                                end = 4.dp,
                                bottom = 16.dp
                            ),
                        onClick = { onSelect(color) }
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
                        topMargin = 12.dp
                    )
                },

            label = { Text(modifier = Modifier, text = stringResource(R.string.group_symbol)) },
            colors = TextFieldDefaults.transparentColorScheme(),
            value = groupName,
            onValueChange = onChangeGroupName
        )

    }

}