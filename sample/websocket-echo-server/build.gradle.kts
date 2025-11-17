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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("io.ktor:ktor-server-netty:3.3.2")
    implementation("io.ktor:ktor-server-core:3.3.2")
    implementation("io.ktor:ktor-websockets:3.3.2")
    implementation("ch.qos.logback:logback-classic:1.5.21")
}
