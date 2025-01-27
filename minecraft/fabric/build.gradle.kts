plugins {
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":minecraft:archCommon").loom.accessWidenerPath
}

val compileModule = configurations.register("compileModule").get()
val common = configurations.register("common").get()
val commonModule = configurations.register("commonModule").get()
val pomCompile = configurations.register("pomDep").get()


configurations.apply {

    named("common").configure {extendsFrom(commonModule)}
    named("compileModule").configure {extendsFrom(commonModule)}

    compileClasspath.configure {extendsFrom(common)}
    runtimeClasspath.configure {extendsFrom(common)}
    named("developmentFabric").configure {extendsFrom(common)}
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.fabric_api_version}")

    commonModule(project(path=":emotesMc", configuration="namedElements")) { isTransitive = false }

    modImplementation("com.terraformersmc:modmenu:${rootProject.modmenu_version}") {
        exclude(group="net.fabricmc.fabric-api")
    }

    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${rootProject.player_animator_version}") {
        include(this)
        pomCompile(this)
    }

    common(project(path=":minecraft:archCommon", configuration="namedElements")) {
        isTransitive = true
        pomCompile(this)
    }
    compileModule(project(path=":minecraft:archCommon", configuration="transformProductionFabric")) {
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
    configurations = listOf(compileModule)
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

if (keysExists) {
    modrinth {
        versionType = project.cfType

        uploadFile = tasks.remapJar.get().outputs

        token = project.keys["modrinth_token"]
        // Get the GitHub Access Token you got from the basics part of this tutorial.
        projectId = "pZ2wrerK" // Enter your modrinth mod ID here.
        //System.out.println("Enter the version number:");
        versionNumber = "${project.mod_version}+${project.minecraft_version}-fabric"
        versionName = project.mod_version

        gameVersions = listOf(project.minecraft_version)
        changelog = changes
        loaders = listOf("fabric", "quilt")
        failSilently = false

        dependencies {
            required.project("fabric-api")
            embedded.project("playeranimator")
        }
    }
}

publishMods {
    modLoaders.add("fabric")
    modLoaders.add("quilt")
    github {
        val token = providers.environmentVariable("GH_TOKEN").orNull
        dryRun = token == null
        accessToken = token
        file.set(tasks.remapJar.get().archiveFile)
        parent(rootProject.tasks.named("publishGithub"))
    }
}
