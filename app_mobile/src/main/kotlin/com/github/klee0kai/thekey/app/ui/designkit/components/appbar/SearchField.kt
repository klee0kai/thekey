package com.github.klee0kai.thekey.app.ui.designkit.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.color.transparentColorScheme
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState

@Composable
fun SearchField(
    textModifier: Modifier = Modifier,
    searchText: String,
    onSearch: (String) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val searchCloseAlpha by animateAlphaAsState(boolean = searchText.isNotBlank())

    Box {
        TextField(
            modifier = textModifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = TextFieldDefaults.transparentColorScheme(),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.4f),
                    text = stringResource(id = R.string.search),
                )
            },
            value = searchText,
            onValueChange = { onSearch(it) }
        )

        if (searchCloseAlpha > 0) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(searchCloseAlpha),
                onClick = { onClose.invoke() },
                content = { Icon(Icons.Filled.Close, contentDescription = null) }
            )
        }
    }
}

@Preview
@Composable
private fun SearchFieldEmptyPreview() = AppTheme {
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
        SearchField(
            textModifier = Modifier,
            searchText = "",
        )
    }
}

@Preview
@Composable
private fun SearchFieldTextPreview() = AppTheme {
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
        SearchField(
            textModifier = Modifier,
            searchText = "some text",
        )
    }
}