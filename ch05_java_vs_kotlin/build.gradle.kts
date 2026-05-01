plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // OkHttp for both Java and Kotlin implementations
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation(kotlin("test"))
    testImplementation(
        "com.squareup.okhttp3:mockwebserver:4.12.0"
    )
}

application {
    mainClass.set("ch05.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
