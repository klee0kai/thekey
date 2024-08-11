package com.github.klee0kai.thekey.core.ui.devkit.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.horizontal
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    onClick: () -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
) {
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    ConstraintLayout(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(
                horizontal = safeContentPaddings.horizontal(minValue = 16.dp),
                vertical = 12.dp
            )
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        val (textField, iconField, hintField) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(textField) {
                    linkTo(
                        start = parent.start,
                        bottom = if (hint.isNotBlank()) hintField.top else parent.bottom,
                        top = parent.top,
                        end = iconField.start,
                        horizontalBias = 0f,
                    )
                },
            text = text,

            )

        if (hint.isNotBlank()) {
            Text(
                modifier = Modifier
                    .alpha(0.4f)
                    .constrainAs(hintField) {
                        linkTo(
                            start = parent.start,
                            bottom = parent.bottom,
                            top = textField.bottom,
                            end = iconField.start,
                            horizontalBias = 0f,
                            topMargin = 2.dp,
                        )
                    },
                text = hint,
                style = MaterialTheme.typography.labelSmall,
            )
        }


        Box(modifier = Modifier
            .constrainAs(iconField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    horizontalBias = 1f,
                )
            }) {
            when {
                icon != null -> icon.invoke()
            }
        }

    }
}


@Preview
@VisibleForTesting
@Composable
fun PreferenceDetailedPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Preference(
        text = "Some Preference",
        hint = "preference hint",
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    )
}


@Preview
@VisibleForTesting
@Composable
fun PreferenceSimplePreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Preference(
        text = "Some Preference",
    )
}

