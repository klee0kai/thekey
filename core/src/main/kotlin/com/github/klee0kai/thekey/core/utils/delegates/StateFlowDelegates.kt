package com.github.klee0kai.thekey.core.utils.delegates

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> MutableStateFlow<T>.delegate() = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this@delegate.value = value
    }
}

fun <T> StateFlow<T>.delegate() = ReadOnlyProperty<Any?, T> { _, _ -> value }