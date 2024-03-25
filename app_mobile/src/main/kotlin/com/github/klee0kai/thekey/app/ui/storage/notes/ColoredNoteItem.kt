package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.model.dummyLazyNote
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.skeleton


@Preview
@Composable
fun ColoredNoteItem(
    modifier: Modifier = Modifier,
    lazyNote: LazyNote = dummyLazyNote(),
) {
    val colorScheme = remember { DI.theme().colorScheme() }
    val note by lazyNote.fullValueFlow().collectAsStateCrossFaded(key = Unit, initial = null)

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (colorGroup, path, description) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(path.top, 4.dp)
                }
        )

        Text(
            text = note.target?.site.takeIf { !it.isNullOrBlank() } ?: stringResource(id = R.string.no_site),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .alpha(note.alpha)
                .skeleton { note.target == null }
                .constrainAs(path) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroup.end,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = 0f,
                    )
                }
        )
        Text(
            text = note.target?.login ?: "",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(note.alpha)
                .skeleton { note.target == null }
                .constrainAs(description) {
                    linkTo(
                        top = path.bottom,
                        bottom = parent.bottom,
                        start = colorGroup.end,
                        end = parent.end,
                        topMargin = 4.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bottomMargin = 6.dp,
                        horizontalBias = 0f,
                        verticalBias = 1f,
                    )
                    top.linkTo(path.bottom, margin = 4.dp)
                }
        )
    }
}