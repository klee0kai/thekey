package com.github.klee0kai.thekey.core.utils.common

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakDelegate<T : Any?> : ReadWriteProperty<Any?, T?> {

    private var ref: WeakReference<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = ref?.get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        ref = WeakReference(value)
    }

    override fun toString(): String = "Weak( ${ref?.get()} )"

}