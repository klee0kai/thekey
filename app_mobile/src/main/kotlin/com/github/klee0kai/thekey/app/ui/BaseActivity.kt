package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.app.ui.navigation.model.RequestPermResult

open class BaseActivity : ComponentActivity() {

    protected val scope = DI.mainThreadScope()
    protected val router get() = DI.router()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        DI.activity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        router.onResult(
            ActivityResult(
                requestCode = requestCode,
                resultCode = resultCode,
                data = data,
            )
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        router.onResult(
            RequestPermResult(
                requestCode = requestCode,
                permissions = permissions.toList(),
                grantResults = grantResults.toList()
            )
        )
    }

}