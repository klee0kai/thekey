package com.github.klee0kai.thekey.app.ui.login.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.Subscription
import kotlinx.parcelize.Parcelize

sealed interface AppOffer : Parcelable {

    /**
     * Describe how app is work.
     * Encryption and storage system
     */
    @Parcelize
    data object HowItWork : AppOffer

    @Parcelize
    data object EnableAnalytics : AppOffer

    @Parcelize
    data object EnableAutoSearch : AppOffer

    @Parcelize
    data object EnableAutoFill : AppOffer

    @Parcelize
    data object EnableBackup : AppOffer

    @Parcelize
    data object RateApp : AppOffer

    @Parcelize
    data class Promo(
        val subscription: Subscription? = null,
    ) : AppOffer


}