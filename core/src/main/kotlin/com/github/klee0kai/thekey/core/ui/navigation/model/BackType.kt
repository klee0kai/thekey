package com.github.klee0kai.thekey.core.ui.navigation.model

enum class BackType {
    /**
     * pop backstack
     */
    Default,

    /**
     * if backstack is empty close app
     */
    FromAppIfNeed,

    /**
     * Close dialog only
     */
    DialogOnly,

    /**
     * close navigation board only
     */
    BoardOnly,
}