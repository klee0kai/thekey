import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.Installation
import com.android.build.api.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.util.Properties

fun BaseAppModuleExtension.appDefaults(namespace: String, project: Project) {
    defaults(namespace, project)
    defaultConfig {
        applicationId = namespace
        targetSdk = 34
        versionCode = 7
        versionName = "0.1.1"
    }
}

fun <BuildFeaturesT : BuildFeatures,
        BuildTypeT : BuildType,
        DefaultConfigT : DefaultConfig,
        ProductFlavorT : ProductFlavor,
        AndroidResourcesT : AndroidResources,
        InstallationT : Installation>
        CommonExtension<BuildFeaturesT, BuildTypeT, DefaultConfigT, ProductFlavorT, AndroidResourcesT, InstallationT>.defaults(namespace: String, project: Project) {
    this.namespace = namespace
    compileSdk = 34

    defaultConfig {
        minSdk = 25
    }

    signingConfigs.register("release") {
        try {
            signingByConfig("private/keystore.properties", project)
            println("commercial signing")
        } catch (e: Exception) {
            println("error to configure commercial signing $e")
            signingByConfig("keystore.properties", project)
            println("community signing")
        }
    }

    composeOptions {
        //  https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


fun ApkSigningConfig.signingByConfig(propertiesFile: String, project: Project) {
    val keystoreProperties = Properties().apply {
        File(project.rootProject.projectDir, propertiesFile).inputStream().use { fis ->
            load(fis)
        }
    }
    storeFile = project.rootProject.file(keystoreProperties.getProperty("storeFile")).also {
        check(it.exists()) { "storeFile $it no exist" }
    }
    storePassword = keystoreProperties.getProperty("storePassword")
    keyAlias = keystoreProperties.getProperty("keyAlias")
    keyPassword = keystoreProperties.getProperty("keyPassword")
}
