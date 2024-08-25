package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.github.klee0kai.thekey.core.ui.navigation.KeyBoardRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext


class KeyboardRouterImpl(
    context: RouterContext
) : KeyBoardRouter, RouterContext by context {

    private val imm by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    }

    override fun hideKeyboard() {
        val view = activity?.currentFocus ?: return
        imm?.hideSoftInputFromWindow(view.windowToken, 0);
    }

}