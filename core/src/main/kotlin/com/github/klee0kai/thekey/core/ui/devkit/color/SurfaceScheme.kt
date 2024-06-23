package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.thekey.core.domain.model.ColorGroup

@Stable
data class SurfaceScheme(
    val surfaceColor: Color,
    val onSurfaceColor: Color,
)

@Stable
data class SurfaceSchemas(
    val noColor: SurfaceScheme,
    val violet: SurfaceScheme,
    val turquoise: SurfaceScheme,
    val pink: SurfaceScheme,
    val orange: SurfaceScheme,
    val coral: SurfaceScheme,
) {

    val colorsGroupCollection: List<SurfaceScheme> = listOf(
        violet, turquoise, pink, orange, coral,
    )

    fun surfaceScheme(group: KeyColor): SurfaceScheme =
        when (group) {
            KeyColor.NOCOLOR -> noColor
            KeyColor.VIOLET -> violet
            KeyColor.TURQUOISE -> turquoise
            KeyColor.PINK -> pink
            KeyColor.ORANGE -> orange
            KeyColor.CORAL -> coral
        }
}


enum class KeyColor {
    NOCOLOR, VIOLET, TURQUOISE, PINK, ORANGE, CORAL;

    companion object {

        val colors get() = entries.filter { it != NOCOLOR }

        val selectableColorGroups get() = colors.map { ColorGroup(id = -it.ordinal.toLong() * 100, keyColor = it) }
    }
}