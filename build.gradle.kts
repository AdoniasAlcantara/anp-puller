import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"

    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("java")
}

group = "io.github.adoniasalcantara"
version = "1.0.0-RC1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("io.ktor:ktor-client-cio:1.5.2")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.14.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "io.github.adoniasalcantara.anp.AppKt",
            "Multi-Release" to "true"
        )
    }
}
