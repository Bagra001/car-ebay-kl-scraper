import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    id("com.vaadin") version "24.0.3"
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

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jsoup:jsoup:1.15.4")
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
