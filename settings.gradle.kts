rootProject.name = "nexproxy"

buildscript {
    repositories {
        mavenCentral()
        maven("https://repo.slne.dev/releases")
        maven("https://repo.slne.dev/snapshots")
    }
    dependencies {
       classpath("dev.slne.surf:surf-api-velocity-api:1.21.10+")

    }

}
aply(plugin = "dev.slne.surf.surfapi.gradle.velocity")

plugins {
    kotlin("jvm") version "1.9.24"
}