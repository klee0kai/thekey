import com.android.build.api.dsl.BuildType
import org.gradle.api.NamedDomainObjectContainer

object BuildTypes {
    const val release = "release"
    const val debug = "debug"
    const val commercialRelease = "commercialRelease"
    const val commercialDebug = "commercialDebug"
}

fun <T : BuildType> NamedDomainObjectContainer<T>.commercialRelease(
    block: T.() -> Unit = {},
) {
    if (!Commercial.isCommercialAvailable) return
    getOrCreate(BuildTypes.commercialRelease) {
        matchingFallbacks += listOf(BuildTypes.release)
    }.also(block)
}

fun <T : BuildType> NamedDomainObjectContainer<T>.commercialDebug(
    block: T.() -> Unit = {},
) {
    if (!Commercial.isCommercialAvailable) return
    getOrCreate(BuildTypes.commercialDebug) {
        matchingFallbacks += listOf(BuildTypes.debug, BuildTypes.commercialDebug, BuildTypes.release)
    }.also(block)
}

private fun <T : BuildType> NamedDomainObjectContainer<T>.getOrCreate(
    name: String,
    initBlock: T.() -> Unit = {},
): T = findByName(name) ?: create(name).also(initBlock)
