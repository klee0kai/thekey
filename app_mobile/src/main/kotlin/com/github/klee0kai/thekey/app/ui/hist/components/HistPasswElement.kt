package com.github.klee0kai.thekey.app.ui.hist.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.skeleton
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha

@Composable
fun HistPasswElement(
    modifier: Modifier = Modifier,
    passw: HistPassw = HistPassw(),
) {
    val theme = LocalTheme.current
    val passwAnimated by animateTargetFaded(target = passw)

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .fillMaxWidth()
    ) {
        val (
            skeletonField,
            passwField,
        ) = createRefs()

        if (!passwAnimated.current.isLoaded) {
            Box(
                modifier = Modifier
                    .alpha(passwAnimated.visibleOnTargetAlpha { !isLoaded })
                    .constrainAs(skeletonField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkToParent(
                            topMargin = 6.dp,
                            bottomMargin = 6.dp,
                        )
                    }
                    .skeleton()
            )
        }

        if (passwAnimated.current.isLoaded) {
            Text(
                text = passwAnimated.current.passw,
                style = theme.typeScheme.body,
                modifier = Modifier
                    .alpha(passwAnimated.alpha)
                    .padding(vertical = 6.dp)
                    .constrainAs(passwField) {
                        width = Dimension.fillToConstraints
                        linkToParent(
                            topMargin = 6.dp,
                            bottomMargin = 6.dp,
                            horizontalBias = 0f,
                        )
                    }
            )
        }
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun HistPasswItemSkeletonPreview() {
    DebugDarkContentPreview {
        HistPasswElement(
            passw = HistPassw(isLoaded = false)
        )
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun HistPasswItemPreview() {
    DebugDarkContentPreview {
        HistPasswElement(
            passw = HistPassw(
                passw = "#1@@134",
                isLoaded = true,
            )
        )
    }
}
