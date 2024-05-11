import com.android.build.api.dsl.AndroidSourceSet
import org.gradle.api.artifacts.dsl.DependencyHandler

object Commercial {
    var isCommercialAvailable = false
}

fun AndroidSourceSet.commercialSourceSet() {
    if (Commercial.isCommercialAvailable) {
        kotlin.srcDirs("src/commercial/kotlin")
        java.srcDirs("src/commercial/java")
        res.srcDirs("src/commercial/res")
        resources.srcDirs("src/commercial/resources")
    } else {
        kotlin.srcDirs("src/community/kotlin")
        java.srcDirs("src/community/java")
        res.srcDirs("src/community/res")
        resources.srcDirs("src/community/resources")
    }
}

fun DependencyHandler.commercialImplementation(dependencyNotation: () -> Any) {
    if (Commercial.isCommercialAvailable) {
        add("implementation", dependencyNotation())
    }
}

fun DependencyHandler.communityImplementation(dependencyNotation: () -> Any) {
    if (!Commercial.isCommercialAvailable) {
        add("implementation", dependencyNotation())
    }
}