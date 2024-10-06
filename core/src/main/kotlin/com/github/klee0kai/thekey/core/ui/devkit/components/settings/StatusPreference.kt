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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateContentSizeProduction
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.createAnchor
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.thedeanda.lorem.LoremIpsum
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun StatusPreference(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    status: String = "",
    statusColor: Color = LocalTheme.current.colorScheme.textColors.hintTextColor,
    onClick: () -> Unit = {},
) {
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val theme = LocalTheme.current
    val statusColorAnimated by animateColorAsState(
        targetValue = statusColor,
        label = "status color"
    )
    val textAnimated by animateTargetFaded(target = text)
    val hintAnimated by animateTargetFaded(target = hint)
    val statusAnimated by animateTargetFaded(target = status)

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
        val (
            textField,
            hintField,
            statusField,
        ) = createRefs()
        val statusAnchorField = createAnchor(horizontalBias = 0.78f, verticalBias = 0.5f)

        Text(
            modifier = Modifier
                .animateContentSizeProduction()
                .alpha(textAnimated.alpha)
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
            text = textAnimated.current,
            style = theme.typeScheme.header,
        )

        if (hint.isNotBlank()) {
            Text(
                modifier = Modifier
                    .animateContentSizeProduction()
                    .alpha(hintAnimated.alpha)
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
                text = hintAnimated.current,
                style = theme.typeScheme.bodySmall,
                color = theme.colorScheme.textColors.hintTextColor,
            )
        }


        Text(
            modifier = Modifier
                .animateContentSizeProduction()
                .alpha(statusAnimated.alpha)
                .constrainAs(statusField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = statusAnchorField.end,
                        bottom = parent.bottom,
                        top = parent.top,
                        end = parent.end,
                        horizontalBias = 0.5f,
                    )
                },
            text = statusAnimated.current,
            textAlign = TextAlign.Center,
            style = theme.typeScheme.buttonText,
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

