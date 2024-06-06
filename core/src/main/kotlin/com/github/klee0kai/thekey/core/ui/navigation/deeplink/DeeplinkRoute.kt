package com.github.klee0kai.thekey.core.ui.navigation.deeplink

import android.content.Intent
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter


fun interface DeeplinkCaseHandler {

    fun AppRouter.handle(intent: Intent): Boolean

}

open class DeeplinkRoute {

    private val handlers = mutableListOf<DeeplinkCaseHandler>()

    fun processDeeplink(intent: Intent, router: AppRouter): Boolean {
        if (handlers.any { with(it) { router.handle(intent) } }) {
            return true
        }
        return false
    }

    fun childIf(condition: (Intent) -> Boolean): DeeplinkRoute {
        val router = DeeplinkRoute()
        handlers.add { intent ->
            if (!condition(intent)) {
                false
            } else {
                router.processDeeplink(intent, this)
            }
        }
        return router
    }

    fun handle(handler: DeeplinkCaseHandler) {
        handlers.add(handler)
    }

}

fun DeeplinkRoute.activity(activityName: String, block: DeeplinkRoute.() -> Unit) {
    childIf { intent -> intent.component?.className == activityName }
        .also(block)
}

fun DeeplinkRoute.action(action: String, block: DeeplinkRoute.() -> Unit) {
    childIf { intent -> intent.action == action }
        .also(block)
}

fun DeeplinkRoute.scheme(scheme: String, block: DeeplinkRoute.() -> Unit) {
    childIf { intent ->
        val uri = intent.data
        uri?.scheme == scheme
    }.also(block)
}

fun DeeplinkRoute.domain(domain: String, block: DeeplinkRoute.() -> Unit) {
    childIf { intent ->
        val uri = intent.data
        uri?.host == domain
    }.also(block)
}

fun DeeplinkRoute.path(path: String, block: DeeplinkRoute.() -> Unit) {
    childIf { intent ->
        val uri = intent.data
        uri?.path?.startsWith(path) ?: false
    }.also(block)
}