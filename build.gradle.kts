import org.jetbrains.changelog.Changelog
import org.gradle.util.GradleVersion

val minimumSupportedGradleVersion = GradleVersion.version("9.0")

check(GradleVersion.current() >= minimumSupportedGradleVersion) {
    "This project requires Gradle ${minimumSupportedGradleVersion.version} or newer. Current version: ${GradleVersion.current().version}."
}

plugins {
    id("java")
    id("org.jetbrains.changelog") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "com.epochbyte"
version = "1.0.2"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    intellijPlatform {
        intellijIdea("2024.2")
        bundledPlugin("org.jetbrains.plugins.terminal")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

    }
}

intellijPlatform {
    pluginConfiguration {
        val changelog = project.changelog
        val pluginVersion = project.version.toString()
        val isSnapshotVersion = pluginVersion.contains("-SNAPSHOT")

        changeNotes = provider {
            with(changelog) {
                val changelogItem = getOrNull(pluginVersion)
                    ?: if (isSnapshotVersion) {
                        getUnreleased()
                    } else {
                        error("CHANGELOG.md is missing a section for version $pluginVersion")
                    }

                renderItem(
                    changelogItem
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = "242"
        }
    }
}

tasks {
    buildSearchableOptions {
        enabled = false
    }
    
    test {
        useJUnitPlatform()
    }
}
