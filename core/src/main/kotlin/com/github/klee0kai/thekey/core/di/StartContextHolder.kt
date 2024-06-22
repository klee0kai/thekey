package com.github.klee0kai.thekey.core.di

import android.content.Context
import java.lang.ref.WeakReference

object StartContextHolder {

    var appRef: WeakReference<Context>? = null

}