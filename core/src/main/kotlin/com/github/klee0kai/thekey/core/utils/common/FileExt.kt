package com.github.klee0kai.thekey.core.utils.common

import java.io.File

val File.parents get() = generateSequence(this) { it.parentFile }