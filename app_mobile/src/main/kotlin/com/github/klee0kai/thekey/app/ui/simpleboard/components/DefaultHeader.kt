package com.github.klee0kai.thekey.app.ui.simpleboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun DefaultHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .defaultMinSize(minHeight = 100.dp, minWidth = 100.dp)
                .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 30.dp),
            painter = painterResource(id = CoreR.drawable.logo_big),
            contentDescription = "key",
        )
    }
}


@Preview
@Composable
fun DefaultHeaderPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DefaultHeader()
}