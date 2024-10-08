package com.github.klee0kai.processor.preview.ext

import javax.lang.model.element.Element

val Element.enclosingElements get() = generateSequence(enclosingElement) { it.enclosingElement }
