package com.github.klee0kai.thekey.app.ui.genhist.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.domain.model.HistPassw
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.utils.views.animateTargetAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.skeleton


@Composable
fun HistPasswItem(
    modifier: Modifier = Modifier,
    passw: HistPassw = HistPassw(),
) {
    val passwAnimated by animateTargetAlphaAsState(target = passw)

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (passwField) = createRefs()

        Text(
            text = passwAnimated.current.passw,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .alpha(passwAnimated.alpha)
                .skeleton(!passwAnimated.current.isLoaded)
                .padding(6.dp)
                .constrainAs(passwField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
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
    }
}

@Preview
@Composable
private fun HistPasswItemSkeletonPreview() = AppTheme {
    HistPasswItem(
        passw = HistPassw(isLoaded = false)
    )
}


@Preview
@Composable
private fun HistPasswItemPreview() = AppTheme {
    HistPasswItem(
        passw = HistPassw(
            passw = "#1@@134",
            isLoaded = true,
        )
    )
}
