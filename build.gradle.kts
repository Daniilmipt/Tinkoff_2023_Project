import org.gradle.jvm.tasks.Jar

plugins {
    java
    id("org.springframework.boot") version "2.7.16"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.2")
    implementation("org.springdoc:springdoc-openapi-data-rest:1.6.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.0")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot2:1.7.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.24.0")
    implementation("com.h2database:h2")
    implementation("org.testcontainers:testcontainers:1.16.3")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.16.3")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}

val fatJar = task("fatJar", type = Jar::class) {
    "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "org.example.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks.withType<Jar> {

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
