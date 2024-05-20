package com.github.klee0kai.thekey.core.utils.views

import android.text.Editable
import android.text.TextWatcher

open class EmptyTextWatcher : TextWatcher {
    var ignoreChanges = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {

    }
}