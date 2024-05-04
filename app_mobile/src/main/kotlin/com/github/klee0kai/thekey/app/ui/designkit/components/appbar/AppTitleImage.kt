package com.github.klee0kai.thekey.app.ui.designkit.components.appbar

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

@Composable
fun AppTitleImage(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.scale(0.5f),
        painter = painterResource(id = R.drawable.logo_big),
        contentDescription = stringResource(id = R.string.app_name),
        contentScale = ContentScale.Inside
    )
}

@Composable
@Preview
private fun AppBarTitlePreview() = AppTheme {
    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        AppTitleImage()
    }
}