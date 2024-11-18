plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.dokka") version "1.9.20"
    id ("jacoco")
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
    //mongodb
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.18.0")
    // Negociacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    // local database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2") // base de datos a usar, puede ser otra
    // Database
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // Caffeine (Para poner un ttl y un limite a la cache)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // websocket mensajes
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(file("build/jacoco"))
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
