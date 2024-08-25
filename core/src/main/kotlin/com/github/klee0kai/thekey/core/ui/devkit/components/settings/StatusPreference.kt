package com.github.klee0kai.thekey.core.ui.devkit.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateContentSizeProduction
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.thedeanda.lorem.LoremIpsum
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun StatusPreference(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    status: String = "",
    statusColor: Color = LocalTheme.current.colorScheme.hintTextColor,
    onClick: () -> Unit = {},
) {
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val theme = LocalTheme.current
    val statusColorAnimated by animateColorAsState(
        targetValue = statusColor,
        label = "status color"
    )

    ConstraintLayout(
        modifier = modifier
            .clickable(onClick = onClick)
            .animateContentSizeProduction()
            .defaultMinSize(minHeight = 56.dp)
            .padding(
                horizontal = safeContentPaddings.horizontal(minValue = 16.dp),
                vertical = 12.dp
            )
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        val (textField, statusField, hintField) = createRefs()
        Text(
            modifier = Modifier
                .animateContentSizeProduction()
                .constrainAs(textField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        bottom = if (hint.isNotBlank()) hintField.top else parent.bottom,
                        top = parent.top,
                        end = statusField.start,
                        horizontalBias = 0f,
                        endMargin = 6.dp,
                    )
                },
            text = text,

            )

        if (hint.isNotBlank()) {
            Text(
                modifier = Modifier
                    .animateContentSizeProduction()
                    .alpha(0.5f)
                    .constrainAs(hintField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            start = parent.start,
                            bottom = parent.bottom,
                            top = textField.bottom,
                            end = statusField.start,
                            horizontalBias = 0f,
                            topMargin = 2.dp,
                            endMargin = 6.dp,
                        )
                    },
                text = hint,
                style = theme.typeScheme.typography.labelSmall,
            )
        }

        Text(
            modifier = Modifier
                .animateContentSizeProduction()
                .constrainAs(statusField) {
                    linkTo(
                        start = parent.start,
                        bottom = parent.bottom,
                        top = parent.top,
                        end = parent.end,
                        horizontalBias = 1f,
                    )
                },
            text = status,
            style = theme.typeScheme.typography.bodyLarge,
            color = statusColorAnimated,
        )
    }
}


@Preview
@VisibleForTesting
@Composable
fun StatusPreferencePreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    val theme = LocalTheme.current

    StatusPreference(
        text = "Some Preference",
        status = "enabled",
        statusColor = theme.colorScheme.greenColor,
    )
}

@Preview
@VisibleForTesting
@Composable
fun StatusPreferenceShortPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    val theme = LocalTheme.current

    StatusPreference(
        text = LoremIpsum.getInstance().getWords(2),
        hint = LoremIpsum.getInstance().getWords(10),
        status = "disabled",
        statusColor = theme.colorScheme.redColor,
    )
}

