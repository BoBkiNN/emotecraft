plugins{
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply true
    id("com.gradleup.shadow") version "8.3.5" apply false

    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("com.modrinth.minotaur") version "2.8.4" apply false
    java
}


subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = rootProject.maven_group

    repositories {
        maven("https://maven.terraformersmc.com/") {
            name = "TerraformersMC maven"
        }
        maven("https://repo.redlance.org/public")
        maven("https://libraries.minecraft.net")
        maven("https://maven.neoforged.net/releases")
    }

    tasks.withType(JavaCompile::class).configureEach {

        //apply plugin: "architectury-plugin"

        val targetVersion = project.java_version
        sourceCompatibility = targetVersion
        targetCompatibility = targetVersion

        options.encoding = "UTF-8"

        //options.compilerArgs << "-Xlint:unchecked"
        //options.deprecation = true	//deprecated warning on compile
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        this.add("implementation", "org.jetbrains:annotations:24.0.1")
    }

    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
        }
    }
}

//---------------- Publishing ----------------

cfType = ENV["RELEASE_TYPE"] ?: "alpha"
isRelease = cfType == "release"
changes = ENV["CHANGELOG"]?.replace("\\\\n", "\n") ?: ""
mod_version = project.version_base

if(!isRelease){
    mod_version = "${project.version_base}-${cfType[0]}.${ ENV["BUILD_NUMBER"]?.let { "build.$it" } ?: getGitRevision()}"
}


lateinit var releaseArtifacts: List<File>

keysExists = ENV["GH_TOKEN"] != null || project.gradle.startParameter.isDryRun

if(keysExists) {
    if (project.gradle.startParameter.isDryRun) {
        println("Dry run, loading publish scripts")
        //All of these are fake, don"t waste your time with it. (Copied from API docs and random generated)
        project.ext.keys["modrinth_token"] = "gho_pJ9dGXVKpfzZp4PUHSxYEq9hjk0h288Gwj4S"
        project.ext.keys["curseforge_key"] = "00000000-0000-0000-0000-000000000000"
        project.ext.keys["github_token"] = "gh_0123456789"
        project.ext.keys["kosmx_maven"] = "V2h5IGRpZCB5b3UgZGVjb2RlIGl0PyAg"
    } else {
        println("Keys loaded, loading publish scripts")
        project.ext.keys["modrinth_token"] = ENV["MODRINTH_TOKEN"] as String
        project.ext.keys["curseforge_key"] = ENV["CURSEFORGE_TOKEN"] as String
        project.ext.keys["github_token"] = ENV["GH_TOKEN"] as String
        project.ext.keys["kosmx_maven"] = ENV["KOSMX_TOKEN"] as String
    }


    githubRelease {
        token(project.keys["github_token"]) // This is your personal access token with Repo permissions
        // You get this from your user settings > developer settings > Personal Access Tokens
        owner = "KosmX"
        // default is the last part of your group. Eg group: "com.github.breadmoirai" => owner: "breadmoirai"
        repo = "emotes" // by default this is set to your project name
        tagName = project.mod_version // by default this is set to "v${project.version}"
        targetCommitish = "dev" // by default this is set to "master"
        releaseName = "Emotecraft-${project.mod_version}" // Release title, by default this is the same as the tagName
        body = changes // by default this is empty
        draft = false // by default this is false
        prerelease = !isRelease // by default this is false
        //releaseAssets = releaseArtifacts
        // this points to which files you want to upload as assets with your release
        //releaseAssets jar.destinationDir.listFiles
        overwrite = true // by default false; if set to true, will delete an existing release with the same tag and name
        dryRun = false // by default false; you can use this to see what actions would be taken without making a release
        apiEndpoint = "https://api.github.com" // should only change for github enterprise users
    }


    tasks.register("autoPublish") {
        //dependsOn(":forge:build",)
        //dependsOn(":fabric:build", ":paper:build")
        dependsOn("collectArtifacts")

        //Configure Modrinth and GitHub with artifacts to release
        doFirst {
            tasks.githubRelease.get().setReleaseAssets(releaseArtifacts)
        }

        finalizedBy(tasks.githubRelease)

        finalizedBy(":minecraft:publishMod")

        finalizedBy(":emotesAPI:publish")
        finalizedBy(":executor:publish")
        finalizedBy(":emotesServer:publish")
        finalizedBy(":emotesAssets:publish")
        finalizedBy(":paper:publish")

        finalizedBy(":paper:modrinth")
        finalizedBy(":bungee:modrinth")
        finalizedBy(":velocity:modrinth")
    }
} else {
    println("Keys are not in ENV, publishing is not possible")
}

//Build all modules task :D
tasks.register("buildAll"){
    dependsOn(":paper:build")
    dependsOn(":bungee:build")
    dependsOn(":velocity:build")
    dependsOn(":minecraft:buildAll")
}

tasks.register("cleanupArtifacts"){
    doLast {
        delete("${project.projectDir}/artifacts")
    }
}

tasks.register("collectArtifacts"){
    dependsOn("cleanupArtifacts")
    dependsOn(":paper:copyArtifacts")
    dependsOn(":bungee:copyArtifacts")
    dependsOn(":velocity:copyArtifacts")
    dependsOn("minecraft:copyArtifacts")
    doLast {
        releaseArtifacts = project.projectDir.toPath().resolve("artifacts").toFile().listFiles().toList()
    }
}

tasks.clean {
    delete("${project.projectDir}/artifacts")
}

