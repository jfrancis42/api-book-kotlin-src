plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "3.0.3"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1"
    )
}

application {
    mainClass.set("ch04.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
