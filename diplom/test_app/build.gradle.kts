import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("org.jmailen.kotlinter") version "3.14.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.20"
    id("org.springframework.boot") version "2.7.14"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "ru.otus.kafka.diplom.testapp"
version = "1.0"

val springdocVersion: String by project
val jacksonVersion: String by project
val flywayVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationJson: String by project
val kotlinCoroutinesJdk8Version: String by project
val kotlinxCoroutinesCoreJVMVersion: String by project
val kotlinReflectVersion: String by project
val postgresVersion: String by project

springBoot {
    mainClass.set("ru.otus.kafka.diplom.testapp.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.springframework.kafka:spring-kafka")

    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation ("org.springdoc:springdoc-openapi-ui:${springdocVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesJdk8Version")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutinesCoreJVMVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")

    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.postgresql:postgresql:${postgresVersion}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}