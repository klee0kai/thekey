plugins {
    kotlin
    kotlin("kapt")
}

dependencies {
    kapt("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.auto.service:auto-service:1.0.1")

    //incap
    implementation("net.ltgt.gradle.incap:incap:0.3")
    implementation("net.ltgt.gradle.incap:incap-processor:0.3")

    //kotlinpoet
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
}