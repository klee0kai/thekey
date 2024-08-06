package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.skeleton
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun ColoredNoteItem(
    modifier: Modifier = Modifier,
    note: ColoredNote = ColoredNote(),
    icon: (@Composable () -> Unit)? = null,
    overlayContent: @Composable () -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current
    val animatedNote by animateTargetCrossFaded(note)
    val skeleton by animateTargetCrossFaded(!note.isLoaded)

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .fillMaxWidth()
    ) {
        val (
            skeletonField,
            colorGroupField,
            siteField, loginField, descriptionField, iconField,
        ) = createRefs()

        if (skeleton.current) {
            Box(
                modifier = Modifier
                    .alpha(skeleton.visibleOnTargetAlpha(true))
                    .skeleton(true)
                    .constrainAs(skeletonField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkTo(
                            top = parent.top,
                            bottom = parent.bottom,
                            start = parent.start,
                            end = parent.end,
                            topMargin = 6.dp,
                            bottomMargin = 6.dp,
                            startMargin = 16.dp,
                            endMargin = 16.dp,
                        )
                    }
            )
        }

        Box(
            modifier = Modifier
                .alpha(skeleton.visibleOnTargetAlpha(false))
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceSchemas.surfaceScheme(animatedNote.current.group.keyColor).surfaceColor,
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
            text = animatedNote.current.site.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.no_site),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .alpha(skeleton.visibleOnTargetAlpha(false))
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
            text = animatedNote.current.login,
            style = MaterialTheme.typography.bodyMedium
                .copy(color = LocalColorScheme.current.androidColorScheme.primary),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .alpha(skeleton.visibleOnTargetAlpha(false))
                .constrainAs(loginField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
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
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .alpha(skeleton.visibleOnTargetAlpha(false))
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


        Box(modifier = Modifier.constrainAs(iconField) {
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

        overlayContent()
    }
}

@VisibleForTesting
@Composable
@Preview
fun ColoredNoteSkeleton() = AppTheme(theme = DefaultThemes.darkTheme) {
    ColoredNoteItem(note = ColoredNote(isLoaded = false))
}

@VisibleForTesting
@Composable
@Preview
fun ColoredNoteDummy() = AppTheme(theme = DefaultThemes.darkTheme) {
    ColoredNoteItem(
        note = ColoredNote(
            site = "some.super.site.com",
            login = "potato",
            desc = "my work note",
            group = ColorGroup(
                name = "CO",
                keyColor = KeyColor.CORAL,
            ),
            isLoaded = true,
        ),
    )
}

@VisibleForTesting
@Composable
@Preview
fun ColoredNoteDummyNoGroup() = AppTheme(theme = DefaultThemes.darkTheme) {
    ColoredNoteItem(
        note = ColoredNote(
            site = "some.super.site.com",
            login = "potato",
            desc = "my work note",
            group = ColorGroup(),
            isLoaded = true,
        )
    )
}

@VisibleForTesting
@Composable
@Preview
fun ColoredNoteIcon() = AppTheme(theme = DefaultThemes.darkTheme) {
    ColoredNoteItem(
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