plugins {
    id("kotlin")
    id("application")
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.5.1")
    implementation("io.ktor:ktor-server-core:1.5.1")
    implementation("io.ktor:ktor-websockets:1.5.1")
    implementation("ch.qos.logback:logback-classic:1.2.9")
}
