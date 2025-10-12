import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.api.tasks.JavaExec

plugins {
    id("java")
    id("application")
    id("jacoco")
}

group = "hse"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.inject:guice:7.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val jacocoExcludes = listOf(
    "org/example/Main*",
    "org/example/di/**"
)

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map { fileTree(it) { exclude(jacocoExcludes) } })
    )
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()
            }
        }
    }
    classDirectories.setFrom(
        files(classDirectories.files.map { fileTree(it) { exclude(jacocoExcludes) } })
    )
}

tasks.check {
    dependsOn("jacocoTestCoverageVerification")
}

application {
    mainClass.set("org.example.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
