package com.github.klee0kai.thekey.core.ui.devkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

/**
 * The font is determined by the properties
 *  - fontSize,
 *  - fontWeight,
 *  - lineHeight
 *  - textIndent
 */
data class TypeScheme(
    @Deprecated("used as a plug for m3")
    val typography: Typography,
    /**
     * main text in list item
     */
    val body: TextStyle,
    /**
     * additional text in list item
     * input hint
     */
    val bodySmall: TextStyle,
    /**
     * appbar header.
     * Tabs header
     * Dialog title
     */
    val screenHeader: TextStyle,

    /**
     * list group header
     * setting item text size
     */
    val header: TextStyle,

    /**
     * Button Text style
     */
    val buttonText: TextStyle,
)