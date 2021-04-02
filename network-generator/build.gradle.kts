/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    id("org.jetbrains.kotlin.jvm") version("1.4.31")
    id("io.gitlab.arturbosch.detekt") version("1.15.0")
    id("org.gradle.maven-publish")
    id("signing")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.4.31")
        classpath("gradle:network-deps:1")
    }
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    implementation(gradleKotlinDsl())

    implementation(Deps.Libs.Jvm.guava)
    implementation(Deps.Libs.Jvm.openApiGenerator)

    compileOnly(Deps.Plugins.kotlinMultiplatform.module!!)

    "detektPlugins"(Deps.Libs.Detekt.detektFormatting)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
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

        signing {
            val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
            val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
            val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                String(Base64.getDecoder().decode(base64Key))
            }
            if (signingKeyId != null) {
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                sign(publishing.publications)
            }
        }
    }
}
