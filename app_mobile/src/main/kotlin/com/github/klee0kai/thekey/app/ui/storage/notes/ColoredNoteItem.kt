package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.domain.model.LazyColoredNote
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColoredNote
import com.github.klee0kai.thekey.app.domain.model.noGroup
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import com.github.klee0kai.thekey.app.utils.lazymodel.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.rememberAlphaAnimate
import com.github.klee0kai.thekey.app.utils.views.rememberSkeletonModifier


@Preview
@Composable
fun ColoredNoteItem(
    modifier: Modifier = Modifier,
    lazyNote: LazyColoredNote = dummyLazyColoredNote(),
) {
    val colorScheme = LocalColorScheme.current
    val animatedNote by lazyNote.collectAsStateCrossFaded()
    val skeletonModifier by rememberSkeletonModifier { animatedNote.current == null }
    val skeletonInvisible by rememberAlphaAnimate { animatedNote.current != null }

    val colorGroup = lazyNote.getOrNull()?.group ?: ColorGroup.noGroup()

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 34.dp)
            .fillMaxWidth()
    ) {
        val (colorGroupField, siteField, loginField, descriptionField) = createRefs()

        Box(
            modifier = Modifier
                .alpha(skeletonInvisible)
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceScheme(colorGroup.keyColor).surfaceColor,
                    shape = RoundedCornerShape(2.dp),
                )
                .constrainAs(colorGroupField) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(siteField.top, 4.dp)
                }
        )

        Text(
            text = animatedNote.current?.site.takeIf { !it.isNullOrBlank() } ?: stringResource(id = R.string.no_site),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .then(skeletonModifier)
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
            text = animatedNote.current?.login.takeIf { !it.isNullOrBlank() } ?: "",
            style = MaterialTheme.typography.bodyMedium
                .copy(color = LocalColorScheme.current.androidColorScheme.primary),
            modifier = Modifier
                .then(skeletonModifier)
                .constrainAs(loginField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = siteField.end,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 8.dp,
                        endMargin = 26.dp,
                        horizontalBias = 0.6f,
                        verticalBias = 0f,
                    )
                }
        )

        Text(
            text = animatedNote.current?.desc.takeIf { !it.isNullOrBlank() } ?: "",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .then(skeletonModifier)
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
    }
}

@Composable
@Preview
private fun ColoredNoteSkeleton() {
    AppTheme {
        ColoredNoteItem(lazyNote = LazyModelProvider(1L) { ColoredNote() })
    }
}

@Composable
@Preview
private fun ColoredNoteDummy() {
    AppTheme {
        ColoredNoteItem(lazyNote = LazyModelProvider(
            1L, preloaded = ColoredNote(
                site = "some.super.site.com",
                login = "potato",
                desc = "my work note",
                group = ColorGroup(
                    name = "CO",
                    keyColor = KeyColor.CORAL,
                )
            )
        ) { ColoredNote() })
    }
}