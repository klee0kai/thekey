package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.basemodel.BaseModel
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.thedeanda.lorem.LoremIpsum
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@Parcelize
data class ColoredOtpNote(
    override val id: Long = 0L,
    val issuer: String = "",
    val name: String = "",
    val otpPassw: String = "",

    val method: OtpMethod = OtpMethod.OTP,
    val nextUpdateTime: Long = 0,
    val interval: Long = 0,

    val group: ColorGroup = ColorGroup.noGroup(),
    override val isLoaded: Boolean = false,
) : Parcelable, BaseModel<Long> {
    companion object;

    override fun filterBy(filter: String): Boolean {
        return issuer.contains(filter, ignoreCase = true)
                || name.contains(filter, ignoreCase = true)
    }

    override fun sortableFlatText(): String = "$issuer-$name"

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

fun ColoredOtpNote.findNextUpdateTime(): ColoredOtpNote {
    val now = System.currentTimeMillis()
    if (interval <= 0) return copy(nextUpdateTime = now + 1.seconds.inWholeMilliseconds)
    return copy(nextUpdateTime = now + 1 + interval - now % interval)
}

@DebugOnly
fun ColoredOtpNote.Companion.dummyLoaded() =
    ColoredOtpNote(
        id = Dummy.dummyId,
        issuer = LoremIpsum.getInstance().url,
        name = LoremIpsum.getInstance().name,
        otpPassw = "123456",
        interval = TimeUnit.SECONDS.toMillis(30),
        nextUpdateTime = System.currentTimeMillis() +
                (TimeUnit.SECONDS.toMillis(30) * 0.8f).toLong(),
        group = ColorGroup.dummy(),
        isLoaded = true,
    )


@DebugOnly
fun ColoredOtpNote.Companion.dummySkeleton() =
    ColoredOtpNote(
        id = Dummy.dummyId,
        isLoaded = false,
    )
