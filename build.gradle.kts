import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"
    id("au.com.dius.pact") version "4.1.15"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:0.15.0")
    testImplementation("au.com.dius.pact.consumer:junit5:4.1.15")
    testImplementation("au.com.dius.pact.provider:junit5spring:4.1.15")
}

val kotlinOptions: org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.() -> Unit = {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions(kotlinOptions)
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions(kotlinOptions)

tasks.test {
    useJUnitPlatform()
    testLogging {
        lifecycle {
            events = setOf(FAILED, PASSED, SKIPPED)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
    systemProperties = mapOf("pact.verifier.publishResults" to true)
}

pact {
    publish {
        pactBrokerUrl = "http://localhost:9292"
    }
}
