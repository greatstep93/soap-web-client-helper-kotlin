import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("http://repo1.maven.org/maven2")
            isAllowInsecureProtocol = true
        }
    }
}

plugins {
    id("org.springframework.boot") version "3.2.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("kapt") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    id("maven-publish")
}

group = "ru.greatstep"
version = "v1.1.1"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

tasks.bootJar {
    enabled = false
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    gradlePluginPortal()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("http://repo1.maven.org/maven2")
        isAllowInsecureProtocol = true
    }
}

val kotlinCoroutinesVersion = "1.8.10"
var kotlinVersion = "1.9.10"
dependencies {
    //SPRING
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.ws:spring-ws-core")


    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    annotationProcessor("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")

    /* LOMBOK */
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    //OTHER
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("jakarta.jws:jakarta.jws-api:3.0.0")
    implementation("jakarta.xml.ws:jakarta.xml.ws-api:4.0.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.yaml:snakeyaml:2.0")
    /* TEST */
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("library") {
            from(components.getByName("java"))
        }
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.javadoc {
    options.encoding = "UTF-8"
}