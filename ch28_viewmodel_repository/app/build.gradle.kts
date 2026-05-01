plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ch28"
    compileSdk = 36

    defaultConfig {
        applicationId = "ch28.vmrepo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )
    implementation(
        "com.jakewharton.retrofit:" +
        "retrofit2-kotlinx-serialization-converter:1.0.0"
    )
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(
        "org.jetbrains.kotlinx:" +
        "kotlinx-serialization-json:1.7.3"
    )
    implementation(
        "org.jetbrains.kotlinx:" +
        "kotlinx-coroutines-android:1.8.1"
    )
    implementation(
        "androidx.appcompat:appcompat:1.7.0"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7"
    )
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.7"
    )
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation(
        "com.squareup.okhttp3:mockwebserver:4.12.0"
    )
    testImplementation(
        "org.jetbrains.kotlinx:" +
        "kotlinx-coroutines-test:1.8.1"
    )
}
