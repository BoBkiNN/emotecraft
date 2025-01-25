plugins {
    id "java"
    id "io.papermc.paperweight.userdev" version "2.0.0-beta.12"
    id "xyz.jpenilla.run-paper" version "2.3.1"
    id "maven-publish"
    id "com.gradleup.shadow"
    id "com.modrinth.minotaur"
}


project.archivesBaseName = project.archives_base_name
//project.version = project.mod_version
version = project.mod_version


repositories {
    mavenCentral()
    maven {
        name = "BucketMaven"
        url "https://hub.spigotmc.org/nexus/content/repositories/snapshots"
    }
    maven {
        name = "dmulloy2"
        url "https://repo.dmulloy2.net/repository/public/"
    }
}

configurations {

    compileModule

    compileClasspath.extendsFrom compileModule
    runtimeClasspath.extendsFrom compileModule

}

dependencies {
    paperweight.paperDevBundle("${rootProject.minecraft_version}-R0.1-SNAPSHOT")

    compileModule(project(":emotesAPI")) { isTransitive = false }
    compileModule(project(":executor")) { isTransitive = false }
    compileModule(project(":emotesServer")) { isTransitive = false }
    compileModule(project(":emotesAssets")) { isTransitive = false }
    compileModule(project(path: ":emotesMc", configuration: "namedElements")) { isTransitive = false }

    compileModule("dev.kosmx.player-anim:anim-core:${rootProject.player_animator_version}") {
        isTransitive false
    }
}

tasks {
    runServer {
        minecraftVersion rootProject.minecraft_version
    }
}

processResources{

    inputs.property("version", project.version)
    inputs.property("description", rootProject.mod_description)

    filesMatching("paper-plugin.yml"){
        expand version: project.version, description: rootProject.mod_description
    }
}

shadowJar {
    configurations = [project.configurations.compileModule]
    archiveClassifier.set("bukkit")
}

jar {
    archiveClassifier.set("bukkit-dev")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

task copyArtifacts{
    dependsOn("build")
    doLast {
        copy{
            from "${project.buildDir}/libs/${project.archives_base_name}-${rootProject.mod_version}-bukkit.jar"
            into "${rootProject.projectDir}/artifacts"
        }
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            // add all the jars that should be included when publishing to maven

            artifactId = "emotesBukkit"

            artifact(jar) {
                classifier ""
            }
            artifact(sourcesJar)


            pom{
                name = "emotesBukkit"
                description = "Minecraft Emotecraft Bukkit plugin"
                url = "https://github.com/KosmX/emotes"
                developers {
                    developer {
                        id = "kosmx"
                        name = "KosmX"
                        email = "kosmx.mc@gmail.com"
                    }
                }

                licenses{
                    license{
                        name = "CC-BY-4.0 License"
                        url = "https://creativecommons.org/licenses/by/4.0/legalcode"
                    }
                }

                scm {
                    connection = "scm:git:github.com/kosmx/emotes.git"
                    developerConnection = "scm:git:github.com/kosmx/emotes.git"
                    url = "https://github.com/KosmX/emotes"
                }
            }
        }
    }

    // select the repositories you want to publish to
    repositories {
        // uncomment to publish to the local maven
        if (project.keysExists) {
            repositories {
                maven {
                    url = "https://maven.kosmx.dev/"
                    credentials {
                        username = "kosmx"
                        password = project.keys.kosmx_maven
                    }
                }
            }
        }
        else {
            mavenLocal()
        }
    }
}

if (keysExists) {
    modrinth {
        versionType = project.cfType
        uploadFile = jar

        token = project.keys.modrinth_token
        projectId = "pZ2wrerK"
        versionNumber = "${project.mod_version}+${project.minecraft_version}-bukkit"
        versionName = "${project.mod_version}"

        gameVersions = [project.minecraft_version]
        changelog = changes
        loaders = ["folia", "paper"]
        failSilently = false
    }
}
