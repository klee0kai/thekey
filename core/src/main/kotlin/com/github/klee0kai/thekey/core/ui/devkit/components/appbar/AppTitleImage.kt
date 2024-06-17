package com.github.klee0kai.thekey.core.ui.devkit.components.appbar

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
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import org.jetbrains.annotations.VisibleForTesting

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

@VisibleForTesting
@Composable
@Preview
fun AppBarTitlePreview2() = AppTheme(theme = DefaultThemes.darkTheme) {
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