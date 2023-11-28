package com.github.klee0kai.thekey.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.ui.designkit.TheKeyTheme
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheKeyTheme {
                MainNavContainer()
            }
        }
    }
}