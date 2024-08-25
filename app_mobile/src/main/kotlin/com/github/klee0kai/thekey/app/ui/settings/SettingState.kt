package com.github.klee0kai.thekey.app.ui.settings

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.domain.model.LoginSecureMode
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingState(
    val analytics: Boolean? = null,
    val loginSecure: LoginSecureMode? = null,
    val encryptionComplexity: NewStorageSecureMode? = null,
    val histPeriod: HistPeriod? = null,
) : Parcelable


