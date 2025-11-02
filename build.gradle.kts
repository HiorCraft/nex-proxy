plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

group = "de.hiorcraft.next"
version = findProperty("version") as String


velocityPluginFile {
    main = "de.hiorcraft.nex.nexproxy.Main"
    name = "NexProxy"

    authors = listOf("HiorCraft")
    version = project.version.toString()
}