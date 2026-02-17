plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.bieniucieniu"
version = "0.0.0"

ktor {
    openApi {
        codeInferenceEnabled = false
        onlyCommented = true
    }
}

application {
    mainClass = "com.bieniucieniu.ApplicationKt"
}

kotlin {
    compilerOptions {
        optIn.add("io.ktor.utils.io.ExperimentalKtorApi")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
    jvmToolchain(21)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.reflect)

    // Ktor Server
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.ktor.server.routing.openapi)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.swagger)

    // Ktor Client
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)

    // Ktor Features
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.simple.memory.cache)

    // Dependency Injection
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // AI
    implementation(libs.koog.ktor)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)

    // Logging
    implementation(libs.logback.classic)

    // Testing
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.test.host)
}
