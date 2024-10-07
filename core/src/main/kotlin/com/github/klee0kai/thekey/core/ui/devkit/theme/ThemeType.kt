package com.github.klee0kai.thekey.core.ui.devkit.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface ThemeType : Parcelable {

    /**
     * Dark theme realization
     */
    @Parcelize
    data object DarkTheme : ThemeType

}