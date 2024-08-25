package com.github.klee0kai.thekey.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.core.ui.navigation.model.RequestPermResult
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseActivity : ComponentActivity() {

    protected open val activityIdentifier: ActivityIdentifier? get() = ActivityIdentifier(this::class.qualifiedName)

    protected val scope = DI.mainThreadScope()
    protected val presenter = DI.mainPresenter()
    protected val router get() = DI.router(activityIdentifier)
    protected val themeManager get() = DI.themeManager(activityIdentifier)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DI.activity(this)
        DI.ctx(applicationContext)
        enableEdgeToEdge()
        subscribeSecurityMode()
    }

    override fun onResume() {
        super.onResume()
        DI.activity(this)
        DI.ctx(applicationContext)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        presenter.windowFocus(hasFocus)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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

    private fun subscribeSecurityMode() {
        scope.launch {
            // prevent screenshots
            presenter.loginSecureMode.collectLatest { sec ->
                when (sec) {
                    LoginSecureMode.LOW_SECURE -> {
                        window.setFlags(0, WindowManager.LayoutParams.FLAG_SECURE)
                    }

                    LoginSecureMode.MIDDLE_SECURE, LoginSecureMode.HARD_SECURE -> {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    }
                }
            }
        }
    }

}