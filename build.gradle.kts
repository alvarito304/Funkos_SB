plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "dev.alvaroherrero"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Database
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // Caffeine (Para poner un ttl y un limite a la cache)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
