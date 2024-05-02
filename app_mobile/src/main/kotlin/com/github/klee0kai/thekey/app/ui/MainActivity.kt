package com.github.klee0kai.thekey.app.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.dynamic.DynamicFeatureInstallDummy
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                DynamicFeatureInstallDummy {
                    MainNavContainer()
                }
            }
        }
        router.initIfNeed(LoginDestination)
    }


}