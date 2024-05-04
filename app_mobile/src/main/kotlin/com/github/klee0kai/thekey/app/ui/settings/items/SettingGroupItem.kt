package com.github.klee0kai.thekey.app.ui.settings.items

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.utils.views.animateTargetCrossFaded
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SettingGroupItem(
    modifier: Modifier = Modifier,
    text: String = "",
    overlayContent: @Composable () -> Unit = {},
) {
    val animatedText by animateTargetCrossFaded(text)

    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .fillMaxWidth()
    ) {

        val (
            groupNameField
        ) = createRefs()
        Text(
            text = animatedText.current,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .alpha(animatedText.alpha)
                .constrainAs(groupNameField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 8.dp,
                        horizontalBias = 0f,
                        verticalBias = 1f,
                    )
                }
        )

        overlayContent()
    }

}

@VisibleForTesting
@Composable
@Preview
fun SettingGroupItemPreview() = AppTheme {
    SettingGroupItem(
        text = "My group"
    )
}