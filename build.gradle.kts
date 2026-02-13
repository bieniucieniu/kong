val koin_version = "4.1.2-Beta1"
val koog_version = "0.5.1"
val kotlin_version = "2.3.0"
val ktor_version = "3.4.0"
val logback_version = "1.5.13"
val exposed_version = "1.0.0"

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
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
    implementation(kotlin("reflect"))

    implementation("com.ucasoft.ktor:ktor-simple-cache:0.57.7")
    implementation("com.ucasoft.ktor:ktor-simple-memory-cache:0.57.7")

    implementation("io.ktor:ktor-server-caching-headers")
    implementation("io.ktor:ktor-server-compression")
    implementation("io.ktor:ktor-server-forwarded-header")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-cio")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-content-negotiation")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-auth")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-content-negotiation")

    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("ai.koog:koog-ktor:$koog_version")
    implementation("io.github.flaxoos:ktor-server-rate-limiting:2.2.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.postgresql:postgresql:42.7.9")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-client-cio-jvm:3.4.0")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

}

