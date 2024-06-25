import com.android.build.api.dsl.DynamicFeatureBuildType

plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("app.cash.paparazzi")
    id("kotlin-parcelize")
}

android {
    defaults("com.github.klee0kai.thekey.dynamic.findstorage", project)
    pushDynamicFeature(this)

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val releaseConf: DynamicFeatureBuildType.() -> Unit = {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release(releaseConf)
        commercialRelease(releaseConf)

        val debugConf: DynamicFeatureBuildType.() -> Unit = {
            isDebuggable = true
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

    // hummus
    implementation(libs.hummus)

    implementation(libs.lorem)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}