package com.github.klee0kai.thekey.app.utils.common

fun Any.invokeReflection(name: String, arg: Any) = runCatching {
    javaClass.methods
        .firstOrNull { it.name == name }
        ?.invoke(this, arg)
}.getOrNull()