import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.1.3"
    id("org.springframework.boot") version "3.1.3"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("com.vaadin") version "24.1.9"
}

group = "de.bagra"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
    maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
}

dependencyManagement {
    val vaadinVersion: String by project
    imports {
        mavenBom("com.vaadin:vaadin-bom:$vaadinVersion")
    }
}

configurations.implementation {
    exclude("ch.qos.logback", "logback-classic")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    val slf4jVersion: String by project
    val jsoupVersion: String by project
    val jakartaVersion: String by project
    val h2Version: String by project

    // slf4j
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    // vaadin
    implementation("com.vaadin:vaadin-spring-boot-starter")

    // spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // jsoup
    implementation("org.jsoup:jsoup:$jsoupVersion")

    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // database
    implementation("com.h2database:h2:$h2Version")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // test
    implementation("org.jsoup:jsoup:1.16.1")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// The following pnpmEnable = true is not needed as pnpm is used by default,
// this is just an example of how to configure the Gradle Vaadin Plugin:
// for more configuration options please see: https://vaadin.com/docs/latest/guide/start/gradle/#all-options
vaadin {
    optimizeBundle = false
}
