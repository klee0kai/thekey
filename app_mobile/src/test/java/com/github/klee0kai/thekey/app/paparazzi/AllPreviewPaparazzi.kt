package com.github.klee0kai.thekey.app.paparazzi

import com.github.klee0kai.thekey.app.ui.gen.preview.allPreviews
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import kotlinx.coroutines.runBlocking
import org.junit.Test

@OptIn(DebugOnly::class)
class AllPreviewPaparazzi : BasePaparazzi() {

    @Test
    fun paparazzi() = runBlocking {
        allPreviews()
            .forEach { preview ->
                runCatching {
                    println("screenshot ${preview.pkg} ${preview.methodName} ")
                    paparazzi.snapshot(
                        preview.methodName,
                    ) {
                        preview.content()
                    }
                }.onFailure {
                    println("$it")
                }
            }
    }

}