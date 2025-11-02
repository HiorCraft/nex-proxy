plugins {
    id("dev.slne.surf.surfapi.gradle.velocity") version "1.3.3"
    kotlin("jvm") version "1.9.24"
}

group = "de.hiorcraft.nex"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.slne.dev/releases")
    maven("https://repo.slne.dev/snapshots")
}

velocityPluginFile {
    main = "de.hiorcraft.nex.nexproxy.Main"
    name = "NexProxy"
    authors = listOf("HiorCraft")
    version = project.version.toString()
}

dependencies {
    api(project(":Nex-Core"))
}