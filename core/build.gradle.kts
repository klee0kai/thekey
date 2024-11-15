plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
}

android {
    defaults("com.github.klee0kai.thekey.core", project)

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.debug)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.stone)
    implementation(libs.bundles.fix.doubles)

    implementation(libs.ml.common)
    implementation(libs.ml.dynamic)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)
    implementation(libs.jetbrain.serialization.json)

    implementation(libs.hummus)

    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    implementation(libs.room.runtime)
    kapt(libs.room.kapt)

    implementation(libs.lorem)

    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.espresso)
}