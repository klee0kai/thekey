package com.github.klee0kai.thekey.app.paparazzi.login

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.login.LoginScreenPreview
import com.github.klee0kai.thekey.app.ui.login.LoginScreenTabletPreview
import org.junit.Test

class LoginScreens : BasePaparazzi() {

    @Test
    fun loginScreenPreview() {
        paparazzi.snapshot {
            LoginScreenPreview()
        }
    }

    @Test
    fun loginScreenTabletPreview() {
        paparazzi.snapshot {
            LoginScreenTabletPreview()
        }
    }

}