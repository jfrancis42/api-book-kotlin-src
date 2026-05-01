plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}
group = "com.example"
version = "1.0"
repositories { mavenCentral() }
val ktorVersion = "3.0.3"
val coroutinesVersion = "1.8.1"
dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation(
        "io.ktor:ktor-client-content-negotiation:$ktorVersion"
    )
    implementation(
        "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
    )
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
    )
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
    )
    testImplementation(kotlin("test"))
    testImplementation(
        "com.squareup.okhttp3:mockwebserver:4.12.0"
    )
    testImplementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
    )
    testImplementation("io.mockk:mockk:1.13.13")
}
application { mainClass.set("ch16.MainKt") }
tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(17) }
