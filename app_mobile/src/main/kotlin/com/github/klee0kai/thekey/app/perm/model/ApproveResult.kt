package com.github.klee0kai.thekey.app.perm.model


enum class ApproveResult {
    /**
     * Permissions granted
     */
    APPROVED,

    /**
     * Permission not requested or granted
     */
    REJECTED,

    /**
     * We can no longer ask the user for permission.
     * It is required to show a window with an explanation and send the user to settings
     */
    REJECTED_FOREVER,

    /**
     * mocked result. We are in view edit mode
     */
    MOCKED,
}
