import me.modmuss50.mpp.ReleaseType

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":minecraft:archCommon").loom.accessWidenerPath
}

val common = configurations.register("common").get()
val shadowCommon = configurations.register("shadowCommon").get()
val pomCompile = configurations.register("pomDep").get()


configurations.apply {
    compileClasspath.configure {extendsFrom(common)}
    runtimeClasspath.configure {extendsFrom(common)}
    named("developmentFabric").configure {extendsFrom(common)}
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.fabric_api_version}")

    shadowCommon(project(path=":emotesMc", configuration="namedElements")) { isTransitive = false }

    modImplementation("com.terraformersmc:modmenu:${rootProject.modmenu_version}") {
        exclude(group="net.fabricmc.fabric-api")
    }

    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${rootProject.player_animator_version}") {
        include(this)
        pomCompile(this)
    }

    shadowCommon(project(":emotesAssets"))

    common(project(path=":minecraft:archCommon", configuration="namedElements")) {
        isTransitive = true
        pomCompile(this)
    }
    shadowCommon(project(path=":minecraft:archCommon", configuration="transformProductionFabric")) {
        isTransitive = true
    }
}


tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("description", rootProject.mod_description)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version, "description" to rootProject.mod_description)
    }
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
}

// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier.set("")
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile.set(tasks.shadowJar.get().archiveFile)
    archiveClassifier.set("")
}

tasks.jar {
    archiveClassifier.set("")
}

components.getByName<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
        skip()
    }
}

tasks.register<Jar>("devJar") {
    from(sourceSets["main"].output)
    archiveClassifier.set("dev")
}

tasks.build {
    dependsOn("devJar")
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            // add all the jars that should be included when publishing to maven

            artifactId = "emotesFabric"

            artifact(tasks.named("devJar"))

            artifact(tasks.remapJar) {
                builtBy(tasks.remapJar)
                classifier = ""
            }

            artifact(tasks.sourcesJar)

            addDeps(project, pomCompile, "compile")
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

publishMods {
    modLoaders.add("fabric")
    modLoaders.add("quilt")
    file.set(tasks.remapJar.get().archiveFile)

    type = ReleaseType.of(if (cfType == "release") "stable" else cfType )
    changelog = changes

    dryRun = providers.environmentVariable("DRY_PUBLISH").isPresent

    github {
        accessToken = providers.environmentVariable("GH_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = providers.gradleProperty("modrinth_id")
        minecraftVersions.add(minecraft_version)
        displayName = mod_version
        version = "${project.mod_version}+${project.minecraft_version}-fabric"

        requires("fabric-api")
        embeds("playeranimator")
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = providers.gradleProperty("curseforge_id_fabric")
        changelogType = "markdown"
        displayName = base.archivesName.get() + "-$mod_version"
        minecraftVersions.add(minecraft_version)

        requires("fabric-api")
        embeds("playeranimator")
    }
}
