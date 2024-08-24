package com.github.klee0kai.thekey.app.ui.settings

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingState(
    val autoSearch: Boolean = false,
    val analytics: Boolean = false,
    val loginSecure: LoginSecureMode = LoginSecureMode.MIDDLE_SECURE,
    val encryptionComplexity: NewStorageSecureMode = NewStorageSecureMode.LOW_SECURE,
    val histPeriod: HistPeriod = HistPeriod.SHORT,
) : Parcelable


