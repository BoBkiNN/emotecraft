@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

architectury {
    minecraft = rootProject.minecraft_version
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    base.archivesName = "${archives_base_name}-${name}-for-MC${minecraft_version}"

    val loom = extensions.getByType(LoomGradleExtensionAPI::class)

    loom.silentMojangMappingsLicense()

    dependencies {
        configurations.getByName("minecraft")("com.mojang:minecraft:${rootProject.minecraft_version}")
        configurations.getByName("mappings")(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${rootProject.minecraft_version}:${rootProject.parchment_version}@zip")
        })
    }
}

allprojects {
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    version = rootProject.mod_version
}

