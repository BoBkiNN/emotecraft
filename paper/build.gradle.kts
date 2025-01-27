import me.modmuss50.mpp.ReleaseType

plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.12"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    `maven-publish`
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
    id("me.modmuss50.mod-publish-plugin") version "0.8.3"
}



base.archivesName = "$archives_base_name-paper"
//project.version = project.mod_version
version = project.mod_version


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots") {
        name = "BucketMaven"
    }
    maven("https://repo.dmulloy2.net/repository/public/") {
        name = "dmulloy2"
    }
}

val compileModule = configurations.register("compileModule").get()
configurations.implementation.configure{extendsFrom(compileModule)}

val compileApi = configurations.register("compileApi").get()
configurations.api.configure{extendsFrom(compileApi)}

dependencies {
    paperweight.paperDevBundle("${rootProject.minecraft_version}-R0.1-SNAPSHOT")

    compileApi(project(":emotesServer")) {
        isTransitive = true
        exclude(group="org.jetbrains", module="annotations")
    }
    compileApi(project(":emotesAssets"))
    compileModule(project(path = ":emotesMc", configuration = "namedElements")) { isTransitive = false }
}

tasks.runServer {
    minecraftVersion(rootProject.minecraft_version)
}

tasks.processResources {

    inputs.property("version", project.version)
    inputs.property("description", rootProject.mod_description)

    filesMatching("paper-plugin.yml"){
        expand("version" to project.version, "description" to rootProject.mod_description)
    }
}

tasks.shadowJar {
    configurations = listOf(compileModule, compileApi)
    archiveClassifier.set("")

    dependencies {
        exclude {
            return@exclude it.moduleGroup.startsWith("com.google")
        }
    }
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.register("copyArtifacts") {
    dependsOn("build")
    doLast {
        copy{
            from("${project.layout.buildDirectory}/libs/${base.archivesName}-${rootProject.mod_version}-paper.jar")
            into ("${rootProject.projectDir}/artifacts")
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            // add all the jars that should be included when publishing to maven

            artifactId = "emotesBukkit"

            // jar only with classes from this module, dependencies will be included in pom
            artifact(tasks.jar) {
                classifier = ""
            }
            artifact(tasks.sourcesJar)
            artifact(tasks.getByName("javadocJar"))
            addDeps(project, compileApi, "compile")
            addDeps(project, configurations.implementation.get(), "runtime")
            withCustomPom("emotesBukkit", "Minecraft Emotecraft Paper plugin")
        }
    }

    repositories {
        if (project.keysExists) {
            kosmxRepo(project)
        } else {
            mavenLocal()
        }
    }
}

if (keysExists) {
    modrinth {
        versionType = cfType
        uploadFile = tasks.shadowJar.get().outputs

        token = project.keys["modrinth_token"]
        projectId = "pZ2wrerK"
        versionNumber = "${project.mod_version}+${project.minecraft_version}-bukkit"
        versionName = project.mod_version

        gameVersions = listOf(project.minecraft_version)
        changelog = changes
        loaders = listOf("folia", "paper")
        failSilently = false
    }
}

publishMods {
    modLoaders.add("paper")
    modLoaders.add("purpur")
    file.set(tasks.shadowJar.get().archiveFile)

    dryRun = providers.environmentVariable("DRY_PUBLISH").isPresent

    github {
        accessToken = providers.environmentVariable("GH_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = providers.gradleProperty("modrinth_id")
        minecraftVersions.add(minecraft_version)
    }
}
