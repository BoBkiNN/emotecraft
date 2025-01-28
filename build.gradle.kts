import me.modmuss50.mpp.ReleaseType

plugins{
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply true
    id("com.gradleup.shadow") version "8.3.5" apply false

    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("com.modrinth.minotaur") version "2.8.4" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.3"
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

    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
        }
    }
}

//---------------- Publishing ----------------

releaseType = ENV["RELEASE_TYPE"] ?: "alpha"
changes = ENV["CHANGELOG"]?.replace("\\\\n", "\n") ?: ""
mod_version = project.version_base

if(releaseType != "stable"){
    mod_version = "${project.version_base}-${releaseType[0]}.${ ENV["BUILD_NUMBER"]?.let { "build.$it" } ?: getGitShortRevision()}"
}
version = mod_version

keysExists = ENV["GH_TOKEN"] != null || project.gradle.startParameter.isDryRun
ext.keys = HashMap()

if(keysExists) {
    if (project.gradle.startParameter.isDryRun) {
        println("Dry run, loading publish scripts")
        //All of these are fake, don"t waste your time with it. (Copied from API docs and random generated)
        project.ext.keys["kosmx_maven"] = "V2h5IGRpZCB5b3UgZGVjb2RlIGl0PyAg"
    } else {
        println("Keys loaded, loading publish scripts")
        project.ext.keys["kosmx_maven"] = providers.environmentVariable("KOSMX_TOKEN")
            .orElse("V2h5IGRpZCB5b3UgZGVjb2RlIGl0PyAg").get()
    }

} else {
    println("Keys are not in ENV, publishing is not possible")
}

publishMods {
    changelog = changes
    type = ReleaseType.of(releaseType)
    dryRun = gradle.startParameter.isDryRun

    github {
        tagName = project.mod_version
        commitish = getGitRevision()
        repository = getGitRepository()
        accessToken = providers.environmentVariable("GH_TOKEN")
        displayName = "Emotecraft-${project.mod_version}"
        allowEmptyFiles = true
    }

}
