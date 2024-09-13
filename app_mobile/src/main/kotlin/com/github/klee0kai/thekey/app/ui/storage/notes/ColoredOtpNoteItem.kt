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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.skeleton
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun ColoredOtpNoteItem(
    modifier: Modifier = Modifier,
    otp: ColoredOtpNote = ColoredOtpNote(),
    icon: (@Composable () -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = LocalColorScheme.current
    val animatedNote by animateTargetCrossFaded(otp)
    val skeleton by animateTargetCrossFaded(!otp.isLoaded)
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()


    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .fillMaxWidth()
    ) {
        val (
            skeletonField,
            colorGroupField,
            siteField, loginField, iconField,
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
                            startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                            endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
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
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    )
                }
        )

        Text(
            text = animatedNote.current.issuer.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.no_issuer),
            style = theme.typeScheme.typography.bodyMedium,
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
                        verticalBias = 0.5f,
                    )
                }
        )

        Text(
            text = animatedNote.current.name,
            style = theme.typeScheme.typography.bodyMedium
                .copy(color = theme.colorScheme.androidColorScheme.primary),
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
                        verticalBias = 0.5f,
                    )
                }
        )

        Box(modifier = Modifier
            .alpha(skeleton.visibleOnTargetAlpha(false))
            .constrainAs(iconField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
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
fun ColoredOtpNoteSkeletonPreview() = DebugDarkContentPreview {
    ColoredOtpNoteItem(otp = ColoredOtpNote(isLoaded = false))
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredOtpNoteDummyPreview() = DebugDarkContentPreview {
    ColoredOtpNoteItem(
        otp = ColoredOtpNote(
            issuer = "some.super.site.com",
            name = "potato",
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
fun ColoredOtpNoteDummyNoGroupPreview() = DebugDarkContentPreview {
    ColoredOtpNoteItem(
        otp = ColoredOtpNote(
            issuer = "some.super.site.com",
            name = "potato",
            group = ColorGroup(),
            isLoaded = true,
        )
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun ColoredOtpNoteDummyIconPreview() = DebugDarkContentPreview {
    ColoredOtpNoteItem(
        otp = ColoredOtpNote(
            issuer = "some.super.site.com",
            name = "potato",
            group = ColorGroup(),
            isLoaded = true,
        ),
        icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "")
        }
    )
}