package com.github.klee0kai.thekey.core.ui.devkit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenuInsetsPreview


@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun DesignScreen() {
    PopupMenuInsetsPreview()
}

