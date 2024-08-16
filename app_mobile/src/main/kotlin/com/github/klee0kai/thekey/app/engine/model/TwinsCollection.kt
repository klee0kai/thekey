package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import kotlinx.parcelize.Parcelize

@Parcelize
@JniPojo
data class TwinsCollection(
    val otpTwins: Array<String> = emptyArray(),
    val loginTwins: Array<String> = emptyArray(),
    val histTwins: Array<String> = emptyArray(),
    val descTwins: Array<String> = emptyArray(),
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwinsCollection

        if (!otpTwins.contentEquals(other.otpTwins)) return false
        if (!loginTwins.contentEquals(other.loginTwins)) return false
        if (!histTwins.contentEquals(other.histTwins)) return false
        if (!descTwins.contentEquals(other.descTwins)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = otpTwins.contentHashCode()
        result = 31 * result + loginTwins.contentHashCode()
        result = 31 * result + histTwins.contentHashCode()
        result = 31 * result + descTwins.contentHashCode()
        return result
    }
}
