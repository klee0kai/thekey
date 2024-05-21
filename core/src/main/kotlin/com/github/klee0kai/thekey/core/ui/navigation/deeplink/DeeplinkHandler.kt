package com.github.klee0kai.thekey.core.ui.navigation.deeplink

import android.content.Intent
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter

class DeeplinkHandler {
    internal val handlers = mutableListOf<DeeplinkCaseHandler>()

    fun handle(intent: Intent, router: AppRouter) = with(router) {
        handlers.any { handler ->
            with(handler) {
                handle(intent)
            }
        }
    }

}

fun interface DeeplinkCaseHandler {

    fun AppRouter.handle(intent: Intent): Boolean

}


fun DeeplinkHandler.raw(block: DeeplinkCaseHandler) {
    handlers.add(block)
}

fun DeeplinkHandler.routeAction(
    action: String? = null,
    alias: String? = null,
    block: DeeplinkCaseHandler,
) {
    handlers.add(DeeplinkCaseHandler { intent ->
        when {
            action != null && intent.action != action -> false
            alias != null && intent.component?.className != alias -> false
            else -> with(block) { handle(intent) }
        }

    })
}