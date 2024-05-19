import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.Installation
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.artifacts.dsl.DependencyHandler

object Commercial {
    var isCommercialAvailable = false
}

fun <BuildFeaturesT : BuildFeatures,
        BuildTypeT : BuildType,
        DefaultConfigT : DefaultConfig,
        ProductFlavorT : ProductFlavor,
        AndroidResourcesT : AndroidResources,
        InstallationT : Installation>
        CommonExtension<BuildFeaturesT, BuildTypeT, DefaultConfigT, ProductFlavorT, AndroidResourcesT, InstallationT>.commercialSourceSets() {
    sourceSets.forEach {
        it.commercialSourceSet()
    }
}

fun AndroidSourceSet.commercialSourceSet() {
    when (name) {
        in listOf(BuildTypes.commercialDebug, BuildTypes.commercialRelease) -> {
            kotlin.srcDirs("src/commercial/kotlin")
            java.srcDirs("src/commercial/java")
            res.srcDirs("src/commercial/res")
            resources.srcDirs("src/commercial/resources")
        }

        in listOf(BuildTypes.debug, BuildTypes.release) -> {
            kotlin.srcDirs("src/community/kotlin")
            java.srcDirs("src/community/java")
            res.srcDirs("src/community/res")
            resources.srcDirs("src/community/resources")
        }
    }
}

fun DependencyHandler.commercialImplementation(dependencyNotation: () -> Any) {
    if (Commercial.isCommercialAvailable) {
        add("commercialReleaseImplementation", dependencyNotation())
        add("commercialDebugImplementation", dependencyNotation())
    }
}
