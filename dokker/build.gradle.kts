/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.31"

    // Apply dokka plugin to generate documentation.
    id("org.jetbrains.dokka") version "1.5.30"

    // Apply the Gradle Qodana plugin for code quality analysis.
    id("org.jetbrains.qodana") version "0.1.13"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // Apply the maven-publish to be able to publish the project to Maven repositories
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "dokker"
            from(components["java"])
            pom {
                name.set("dokker")
                description.set("The Kotlin Way (DSL) to Generate Many Dockerfiles At Once")
                url.set("https://github.com/tiulpin/dokker")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/dokker.git")
                    developerConnection.set("scm:git:git@github.com:tiulpin/dokker.git")
                    url.set("https://github.com/tiulpin/dokker")
                }
                developers {
                    developer {
                        id.set("tiulpin")
                        name.set("Viktor Tiulpin")
                        email.set("viktor@tiulp.in")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/sa/maven-public")
            credentials {
                username = System.getenv("SPACE_USERNAME")
                password = System.getenv("SPACE_PASSWORD")
            }
        }
    }
}

qodana {
    dockerImageName.set("jetbrains/qodana-jvm-community")
    showReport.set(true)
    cachePath.set(".qodana/")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
