import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import org.gradle.api.JavaVersion

fun BaseAppModuleExtension.defaults(namespace: String) {
    this.namespace = namespace
    compileSdk = 34

    defaultConfig {
        applicationId = namespace
        minSdk = 25
        targetSdk = 34
        versionCode = 6
        versionName = "0.1.0"
    }

    composeOptions {
        //  https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

fun DynamicFeatureExtension.defaults(namespace: String) {
    this.namespace = namespace
    compileSdk = 34

    defaultConfig {
        minSdk = 25
    }

    composeOptions {
        //  https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}