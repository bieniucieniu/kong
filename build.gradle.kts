val koin_version: String by project
val koog_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}

group = "com.bieniucieniu"
version = "0.0.1"

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}

application {
    mainClass = "com.bieniucieniu.ApplicationKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("io.ktor:ktor-server-caching-headers")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-forwarded-header")
    implementation("io.ktor:ktor-server-core")
    implementation("com.ucasoft.ktor:ktor-simple-cache:0.57.7")
    implementation("com.ucasoft.ktor:ktor-simple-memory-cache:0.57.7")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("ai.koog:koog-ktor:$koog_version")
    implementation("io.github.flaxoos:ktor-server-rate-limiting:2.2.1")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.0")
    implementation("io.ktor:ktor-client-apache:3.4.0")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

}

