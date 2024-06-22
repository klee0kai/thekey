package com.github.klee0kai.thekey.core.utils.log

import timber.log.Timber

class TimberWtfTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= PRIORITY_ASSERT) {
            throw IllegalStateException(message, t)
        }
    }

    companion object {
        private const val PRIORITY_ASSERT = 7
    }

}