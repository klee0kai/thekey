@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote


@Preview
@Composable
fun ColoredNoteItem(
    modifier: Modifier = Modifier,
    note: DecryptedNote = DecryptedNote(
        site = "site",
        login = "login",
        desc = "description",
    )
) {
    val colorScheme = remember { DI.theme().colorScheme() }

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
            text = note.site,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
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
            text = note.login,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
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