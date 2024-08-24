package com.github.klee0kai.thekey.core.domain.model

enum class LoginSecureMode {
    /**
     * Automatic logout from storage occurs
     * after 10 minutes of application minimization.
     */
    LOW_SECURE,

    /**
     * Automatic logout from storage occurs
     * after 1 minute of application minimization.
     *
     * Screenshots on screens are blocked.
     */
    MIDDLE_SECURE,

    /**
     * Automatic logout from storage
     * is performed immediately when the application is minimized.
     *
     *  Screenshots on screens are blocked.
     *  Screen overlap detection is enabled, blocking content substitution.
     */
    HARD_SECURE,
}

fun LoginSecureMode.nextRecursive(): LoginSecureMode = when (this) {
    LoginSecureMode.LOW_SECURE -> LoginSecureMode.MIDDLE_SECURE
    LoginSecureMode.MIDDLE_SECURE -> LoginSecureMode.HARD_SECURE
    LoginSecureMode.HARD_SECURE -> LoginSecureMode.LOW_SECURE
}