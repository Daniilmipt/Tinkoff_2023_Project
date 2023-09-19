import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.5.20-RC"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.testng:testng:7.7.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks.test {
    useJUnitPlatform()
}

val fatJar = task("fatJar", type = Jar::class) {
    var baseName = "${project.name}-fat"
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

tasks.withType<Jar>() {

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
