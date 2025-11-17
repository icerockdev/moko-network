/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.gradle.plugin-publish") version ("0.15.0")
    id("java-gradle-plugin")
}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.mokoGradlePlugin)
        classpath(libs.kotlinSerializationGradlePlugin)
    }
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "dev.icerock.moko.gradle.detekt")
apply(plugin = "dev.icerock.moko.gradle.publication")
apply(plugin = "dev.icerock.moko.gradle.publication.nexus")

group = "dev.icerock.moko"
version = libs.versions.mokoNetworkVersion.get()

dependencies {
    implementation(gradleKotlinDsl())
    compileOnly(libs.kotlinGradlePlugin)
    implementation(libs.guava)
    implementation(libs.openApiGenerator)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

configure<PublishingExtension> {
    publications.register("mavenJava", MavenPublication::class) {
        from(components["java"])
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

gradlePlugin {
    plugins {
        create("multiplatform-network-generator") {
            id = "dev.icerock.mobile.multiplatform-network-generator"
            implementationClass = "dev.icerock.moko.network.MultiPlatformNetworkGeneratorPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/icerockdev/moko-network"
    vcsUrl = "https://github.com/icerockdev/moko-network"
    description = "Plugin to provide network components for iOS & Android"
    tags = listOf("moko-network", "moko", "kotlin", "kotlin-multiplatform")

    plugins {
        getByName("multiplatform-network-generator") {
            displayName = "MOKO network generator plugin"
        }
    }

    mavenCoordinates {
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String
    }
}