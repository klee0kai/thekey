package com.github.klee0kai.thekey.app.ui.navigation.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class SimpleActivityContract : ActivityResultContract<Intent, Intent?>() {
    override fun createIntent(context: Context, input: Intent): Intent = input
    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = intent
}