import me.modmuss50.mpp.ReleaseType

plugins{
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply true
    id("com.gradleup.shadow") version "8.3.5" apply false

    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("com.modrinth.minotaur") version "2.8.4" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
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

shouldPublishMaven = providers.environmentVariable("KOSMX_TOKEN").getOrElse("").isNotBlank()

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
