package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.OtpAlgo
import com.github.klee0kai.thekey.core.domain.model.OtpMethod
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class DecryptedOtpNote(
    val ptnote: Long = 0L,
    val issuer: String = "",
    val name: String = "",

    val url: String = "",
    val secret: String = "",
    val pin: String = "",
    val otpPassw: String = "",
    val otpMethodRaw: Int = OtpMethod.TOTP.code,
    val otpAlgoRaw: Int = OtpAlgo.SHA1.code,
    val digits: Int = 6,
    val interval: Int = 30,
    val counter: Int = 0,

    val crTime: Long = 0,
    val colorGroupId: Long = 0,
) : Parcelable {

    val otpMethod: OtpMethod
        get() = OtpMethod.from(otpMethodRaw)

    val otpAlgo: OtpAlgo
        get() = OtpAlgo.from(otpAlgoRaw)

}

fun DecryptedOtpNote.isEmpty(): Boolean =
    issuer.isEmpty() && name.isEmpty() && secret.isEmpty() && url.isEmpty()


fun DecryptedOtpNote.coloredNote(
    group: ColorGroup? = null,
    isLoaded: Boolean = false,
) = ColoredOtpNote(
    ptnote = ptnote,

    group = group ?: ColorGroup(id = colorGroupId),
    isLoaded = isLoaded,
)
