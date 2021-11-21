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

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            create<MavenPublication>("mavenJava") {
                pom {
                    name.set("My Library")
                    description.set("A concise description of my library")
                    url.set("https://tiulp.in/")
                    properties.set(mapOf(
                        "myProp" to "value",
                        "prop.with.dots" to "anotherValue"
                    ))
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
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
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tiulpin/library")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
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

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation("com.google.guava:guava:30.1.1-jre")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")
}
