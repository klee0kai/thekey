import com.android.build.api.dsl.DynamicFeatureBuildType

plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("app.cash.paparazzi")
}

android {
    defaults("com.github.klee0kai.thekey.feature.qrcodescanner", project)
    pushDynamicFeature(this)

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val releaseConf: DynamicFeatureBuildType.() -> Unit = {
            proguardFiles("proguard-rules.pro")
        }
        release(releaseConf)
        commercialRelease(releaseConf)

        val debugConf: DynamicFeatureBuildType.() -> Unit = {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles("proguard-rules.pro")
        }
        debug(debugConf)
        commercialDebug(debugConf)
    }
    commercialSourceSets()

    buildFeatures {
        compose = true
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":app_mobile"))
    implementation(project(":core"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.debug)
    implementation(libs.bundles.androidx)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)

    // stone
    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    // BarCodeScan
    implementation(libs.ml.dynamic)
    implementation(libs.ml.barcode)
    implementation(libs.ml.camera)
    implementation(libs.ml.vision)

    // hummus
    implementation(libs.hummus)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}