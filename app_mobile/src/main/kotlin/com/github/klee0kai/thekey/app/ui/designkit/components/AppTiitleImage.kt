package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R

@Composable
@Preview
fun AppTitleImage(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.logo_big),
        contentDescription = stringResource(id = R.string.app_name),
        contentScale = ContentScale.Inside,
        modifier = modifier.scale(0.5f)
    )
}