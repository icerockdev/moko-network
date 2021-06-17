/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    id("io.gitlab.arturbosch.detekt") version("1.15.0") apply(false)
}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.15.1")
        classpath("dev.icerock.moko:network-generator") // substituted
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.10")
        classpath("dev.icerock:mobile-multiplatform:0.9.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.android.tools.build:gradle:4.2.1")
    }
}

allprojects {

    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        input.setFrom("src/commonMain/kotlin", "src/androidMain/kotlin", "src/iosMain/kotlin")
    }

    dependencies {
        "detektPlugins"(rootProject.libs.detektFormatting)
    }

    configurations.configureEach {
        resolutionStrategy {
            force("${rootProject.libs.coroutines.get().module}:${rootProject.libs.coroutines.get().versionConstraint}")
        }
    }

    plugins.withId("com.android.library") {
        configure<com.android.build.gradle.LibraryExtension> {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())

            defaultConfig {
                minSdkVersion(libs.versions.minSdk.get().toInt())
                targetSdkVersion(libs.versions.targetSdk.get().toInt())
            }
        }
    }

    plugins.withId("org.gradle.maven-publish") {
        group = "dev.icerock.moko"
        version = libs.versions.mokoNetworkVersion.get()

        val javadocJar by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        configure<PublishingExtension> {
            repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "OSSRH"

                credentials {
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_KEY")
                }
            }

            publications.withType<MavenPublication> {
                // Stub javadoc.jar artifact
                artifact(javadocJar.get())

                // Provide artifacts information requited by Maven Central
                pom {
                    name.set("MOKO network")
                    description.set("Network components with codegeneration of rest api for mobile (android & ios) Kotlin Multiplatform development")
                    url.set("https://github.com/icerockdev/moko-network")
                    licenses {
                        license {
                            url.set("https://github.com/icerockdev/moko-network/blob/master/LICENSE.md")
                        }
                    }

                    developers {
                        developer {
                            id.set("Alex009")
                            name.set("Aleksey Mikhailov")
                            email.set("aleksey.mikhailov@icerockdev.com")
                        }
                        developer {
                            id.set("Tetraquark")
                            name.set("Vladislav Areshkin")
                            email.set("vareshkin@icerockdev.com")
                        }
                        developer {
                            id.set("Dorofeev")
                            name.set("Andrey Dorofeev")
                            email.set("adorofeev@icerockdev.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:ssh://github.com/icerockdev/moko-network.git")
                        developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-network.git")
                        url.set("https://github.com/icerockdev/moko-network")
                    }
                }
            }

            apply(plugin = "signing")

            configure<SigningExtension> {
                val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
                val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
                val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                    String(Base64.getDecoder().decode(base64Key))
                }
                if (signingKeyId != null) {
                    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                    sign(publications)
                }
            }
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
