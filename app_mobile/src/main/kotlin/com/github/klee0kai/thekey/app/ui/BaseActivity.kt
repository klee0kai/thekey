package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.core.ui.navigation.model.RequestPermResult
import kotlinx.coroutines.cancelChildren

open class BaseActivity : ComponentActivity() {

    protected open val activityIdentifier: ActivityIdentifier? get() = ActivityIdentifier(this::class.qualifiedName)

    protected val scope = DI.mainThreadScope()
    protected val router get() = DI.router(activityIdentifier)
    protected val themeManager get() = DI.themeManager(activityIdentifier)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DI.activity(this)
        DI.ctx(applicationContext)

        enableEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        DI.activity(this)
        DI.ctx(applicationContext)
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

    override fun onDestroy() {
        super.onDestroy()
        scope.coroutineContext.cancelChildren()
    }

}