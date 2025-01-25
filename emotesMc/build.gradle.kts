@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
}

loom {
    silentMojangMappingsLicense()
}

version = rootProject.mod_version

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.minecraft_version}")
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${rootProject.minecraft_version}:${rootProject.parchment_version}@zip")
    })

    api(project(":emotesServer"))
}

tasks.remapJar {
    enabled = false
}
