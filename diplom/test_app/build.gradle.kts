import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("org.jmailen.kotlinter") version "3.14.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.20"
    id("org.springframework.boot") version "2.7.14"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.8.20"
}

group = "ru.otus.kafka.diplom.testapp"
version = "1.0"

val springdocVersion: String by project
val jacksonVersion: String by project
val flywayVersion: String by project
val kotlinxCoroutinesVersion: String by project

/*springBoot {
    mainClass.set("ru.otus.kafka.diplom.testapp.MainKt")
}*/


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.springframework.kafka:spring-kafka")

    implementation("org.springdoc:springdoc-openapi-ui:${springdocVersion}")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}