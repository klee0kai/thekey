package com.github.klee0kai.thekey.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigation.AppRouterImp
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainNavContainer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DI.activity(this)
        (DI.router() as? AppRouterImp)?.activity = this
    }

}