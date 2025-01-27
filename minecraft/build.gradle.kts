@file:Suppress("UnstableApiUsage")

import com.matthewprenger.cursegradle.CurseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin")
    id("com.matthewprenger.cursegradle")
}

architectury {
    minecraft = rootProject.minecraft_version
}

subprojects {
    apply(plugin = "dev.architectury.loom")

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

tasks.named("publish") {
    if (keysExists) {
        finalizedBy(":minecraft:fabric:modrinth")
        finalizedBy(":minecraft:neoforge:modrinth")
        finalizedBy(":minecraft:publishToCF")
    }
}

if(keysExists) {

    curseforge {
        apiKey = project.keys["curseforge_key"]

        project {
            id = "397809" //Fabric version
            changelogType = "markdown"
            //changelog = "[See on Github](https://github.com/KosmX/emotes/commits/master)"
            changelog = changes
            releaseType = project.cfType
            addGameVersion(rootProject.minecraft_version)
            addGameVersion("Fabric")
            addGameVersion("Quilt")

            relations(delegateClosureOf<CurseRelation> {
                requiredDependency("fabric-api")
                embeddedLibrary("playeranimator")
            })

            options(delegateClosureOf<Options> {
                forgeGradleIntegration = false // FABRIC MOD
                javaVersionAutoDetect = false // defaults to true
            })

            mainArtifact("${project.projectDir}/fabric/build/libs/${base.archivesName}-${project.version}.jar")
        }

        project {
            id = "403422" //Forge version
            changelogType = "markdown"
            //changelog = "[See on Github](https://github.com/KosmX/emotes/commits/master)"
            changelog = changes
            releaseType = project.cfType
            addGameVersion(rootProject.minecraft_version)
            addGameVersion("NeoForge")

            relations(delegateClosureOf<CurseRelation> {
                embeddedLibrary("playeranimator")
            })

            options(delegateClosureOf<Options> {
                forgeGradleIntegration = false // ARCHITECTURY MOD
                javaVersionAutoDetect = false // defaults to true
            })

            mainArtifact("${project.projectDir}/neoforge/build/libs/${base.archivesName}-${project.version}.jar")
        }

    }

    tasks.register("publishToCF") {
        dependsOn("buildAll")
        finalizedBy(tasks.curseforge)
    }
}

fun CurseExtension.project(conf: CurseProject.() -> Unit) {
    val curseProject = CurseProject()
    curseProject.apply(conf)
    if (curseProject.apiKey == null) {
        curseProject.apiKey = this.apiKey
    }
    curseProjects.add(curseProject)
}
