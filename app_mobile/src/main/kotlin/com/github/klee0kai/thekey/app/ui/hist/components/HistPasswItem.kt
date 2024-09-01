package com.github.klee0kai.thekey.app.ui.hist.components

import androidx.compose.foundation.layout.defaultMinSize
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
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.skeleton

@Composable
fun HistPasswItem(
    modifier: Modifier = Modifier,
    passw: HistPassw = HistPassw(),
) {
    val passwAnimated by animateTargetCrossFaded(target = passw)

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
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
                .padding(vertical = 6.dp)
                .constrainAs(passwField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        horizontalBias = 0f,
                        verticalBias = 0f,
                    )
                }
        )
    }
}

@Preview
@Composable
private fun HistPasswItemSkeletonPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    HistPasswItem(
        passw = HistPassw(isLoaded = false)
    )
}

@Preview
@Composable
private fun HistPasswItemPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    HistPasswItem(
        passw = HistPassw(
            passw = "#1@@134",
            isLoaded = true,
        )
    )
}