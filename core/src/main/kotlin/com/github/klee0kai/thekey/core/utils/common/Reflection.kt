package com.github.klee0kai.thekey.core.utils.common

object JvmReflection {

    fun Any.invokeReflection(name: String, arg: Any) = runCatching {
        javaClass.methods
            .firstOrNull { it.name == name }
            ?.invoke(this, arg)
    }.getOrNull()

    inline fun <reified T> createNew(name: String) = runCatching {
        val namedClass = Class.forName(name)
        val obj = namedClass.constructors.firstOrNull()?.newInstance()
        obj as? T
    }.getOrNull()

}

