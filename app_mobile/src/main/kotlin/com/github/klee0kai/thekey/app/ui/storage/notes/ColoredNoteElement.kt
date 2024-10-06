package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.skeleton
import com.thedeanda.lorem.LoremIpsum
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun ColoredNoteElement(
    modifier: Modifier = Modifier,
    note: ColoredNote = ColoredNote(),
    icon: (@Composable () -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = LocalColorScheme.current
    val animatedNote by animateTargetFaded(note)
    val skeletonAlpha by animateAlphaAsState(!note.isLoaded)
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .fillMaxWidth()
    ) {
        val (
            skeletonField,
            colorGroupField,
            siteField, loginField, descriptionField, iconField,
        ) = createRefs()

        if (skeletonAlpha > 0) {
            Box(
                modifier = Modifier
                    .alpha(skeletonAlpha)
                    .skeleton(true)
                    .constrainAs(skeletonField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkToParent(
                            topMargin = 6.dp,
                            bottomMargin = 6.dp,
                            startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                            endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                        )
                    }
            )
        }

        Box(
            modifier = Modifier
                .alpha(1f - skeletonAlpha)
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceSchemas.surfaceScheme(animatedNote.current.group.keyColor).surfaceColor,
                    shape = RoundedCornerShape(2.dp),
                )
                .constrainAs(colorGroupField) {
                    linkToParent(
                        verticalBias = 0.5f,
                        horizontalBias = 0f,
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    )
                }
        )

        Text(
            text = animatedNote.current.site.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.no_site),
            style = theme.typeScheme.body,
            modifier = Modifier
                .alpha(1f - skeletonAlpha)
                .constrainAs(siteField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
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
            text = animatedNote.current.login,
            style = theme.typeScheme.body
                .copy(color = theme.colorScheme.textColors.primaryTextColor),
            modifier = Modifier
                .alpha(1f - skeletonAlpha)
                .constrainAs(loginField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        start = siteField.end,
                        end = iconField.start,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 8.dp,
                        endMargin = 4.dp,
                        horizontalBias = 0.6f,
                        verticalBias = 0f,
                    )
                }
        )

        Text(
            text = animatedNote.current.desc,
            color = theme.colorScheme.textColors.bodyTextColor,
            style = theme.typeScheme.bodySmall,
            modifier = Modifier
                .alpha(1f - skeletonAlpha)
                .constrainAs(descriptionField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        top = siteField.bottom,
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


        Box(modifier = Modifier
            .alpha(1f - skeletonAlpha)
            .constrainAs(iconField) {
                linkToParent(
                    startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    horizontalBias = 1f,
                )
            }) {
            when {
                icon != null -> icon.invoke()
            }
        }

    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredNoteSkeleton() = DebugDarkContentPreview {
    ColoredNoteElement(note = ColoredNote(isLoaded = false))
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredNoteDummy() = DebugDarkContentPreview {
    ColoredNoteElement(
        note = ColoredNote(
            site = LoremIpsum.getInstance().url,
            login = LoremIpsum.getInstance().getWords(1),
            desc = LoremIpsum.getInstance().getWords(6),
            group = ColorGroup(
                name = "CO",
                keyColor = KeyColor.CORAL,
            ),
            isLoaded = true,
        ),
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredNoteDummyNoGroup() = DebugDarkContentPreview {
    ColoredNoteElement(
        note = ColoredNote(
            site = "some.super.site.com",
            login = "potato",
            desc = "my work note",
            group = ColorGroup(),
            isLoaded = true,
        )
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredNoteIcon() = DebugDarkContentPreview {
    ColoredNoteElement(
        note = ColoredNote(
            site = "some.super.site.com",
            login = "potato",
            desc = "my work note",
            group = ColorGroup(),
            isLoaded = true,
        ),
        icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "")
        }
    )
}