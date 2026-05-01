plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.7"
}
group = "com.example"
version = "0.0.1-SNAPSHOT"
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
repositories { mavenCentral() }
dependencies {
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-web"
    )
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-data-jpa"
    )
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-validation"
    )
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-security"
    )
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-oauth2-resource-server"
    )
    implementation(
        "org.springframework.boot:" +
        "spring-boot-starter-actuator"
    )
    implementation(
        "io.micrometer:" +
        "micrometer-registry-prometheus"
    )
    implementation(
        "com.fasterxml.jackson.module:" +
        "jackson-module-kotlin"
    )
    implementation(
        "org.jetbrains.kotlin:kotlin-reflect"
    )
    implementation(
        "com.bucket4j:bucket4j-core:8.10.1"
    )
    runtimeOnly("com.h2database:h2")
    testImplementation(
        "org.springframework.boot:" +
        "spring-boot-starter-test"
    )
    testImplementation(
        "org.springframework.security:" +
        "spring-security-test"
    )
    testImplementation(
        "org.mockito.kotlin:mockito-kotlin:5.4.0"
    )
    testRuntimeOnly(
        "org.junit.platform:junit-platform-launcher"
    )
}
tasks.withType<Test> { useJUnitPlatform() }
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
