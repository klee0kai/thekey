package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
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

enum class OtpMethod(val code: Int) {
    OTP(0),
    HOTP(1),
    TOTP(2),
    YAOTP(3);

    companion object {
        fun from(code: Int): OtpMethod {
            return entries.firstOrNull { it.code == code } ?: OTP
        }
    }
}

enum class OtpAlgo(val code: Int) {
    SHA1(0),
    SHA256(1),
    SHA512(2);

    companion object {
        fun from(code: Int): OtpAlgo {
            return entries.firstOrNull { it.code == code } ?: SHA1
        }
    }
}

fun DecryptedOtpNote.isEmpty(): Boolean =
    issuer.isEmpty() && name.isEmpty() && secret.isEmpty() && url.isEmpty()