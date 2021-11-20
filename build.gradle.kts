import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
}

group = "com.github.pool_party.spotivibe"
version = "0.1"

repositories {
    maven("https://jitpack.io")
    mavenCentral()

    flatDir {
        dirs = mutableSetOf(file("dependencies"))
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-protobuf", "1.2.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.1")

    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha2")
    implementation("io.github.microutils", "kotlin-logging", "2.0.11")
    implementation("com.natpryce", "konfig", "1.6.10.0")
    implementation("com.github.elbekD", "kt-telegram-bot", "1.3.8")
    implementation("org.pool-party", "flume", "0.0.1")

    implementation("com.adamratzman", "spotify-api-kotlin-core", "3.8.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["compileClasspath"].forEach { from(zipTree(it.absoluteFile)) }
    configurations["runtimeClasspath"].forEach { from(zipTree(it.absoluteFile)) }

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.github.pool_party.spotivibe.MainKt"
            )
        )
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}

tasks.register("stage") {
    dependsOn("build", "clean")
}
