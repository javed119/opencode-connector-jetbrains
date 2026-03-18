plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.epochbyte"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

intellij {
    version.set("2024.2")
    type.set("IC")
    plugins.set(listOf("org.jetbrains.plugins.terminal"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("242")
        // 不设置 untilBuild，支持所有未来版本
    }
    
    buildSearchableOptions {
        enabled = false
    }
    
    test {
        useJUnitPlatform()
    }
}
