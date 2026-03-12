plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.epochbyte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

intellij {
    version.set("2023.2")
    type.set("IC")
    plugins.set(listOf("org.jetbrains.plugins.terminal"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("999.*")
    }
    
    test {
        useJUnitPlatform()
    }
}