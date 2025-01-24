import groovy.util.Node
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.internal.extensions.core.extra
import java.util.Properties

plugins {
    java
    `java-library`
    //id "com.github.johnrengelman.shadow" version "6.1.0"
    `maven-publish`
    id("com.modrinth.minotaur")
}


base.archivesName = properties["archives_base_name"] as String
//project.version = project.mod_version
version = properties["mod_version"]!!.toString()


repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots") {
        name = "BucketMaven"
    }
    maven("https://repo.dmulloy2.net/repository/public/") {
        name = "dmulloy2"
    }
}

configurations {

    val compileModule = create("compileModule")
    implementation {extendsFrom(compileModule)}

    val compileApi = create("compileApi")
    api {extendsFrom(compileApi)}
}

/**
 * Dependencies embedded to jar.
 */
val compileModule
    get() = configurations.getByName("compileModule")

/**
 * Dependencies embedded to jar, has compile maven scope
 */
val compileApi
    get() = configurations.getByName("compileApi")

infix fun Project.p(prop: String): String? = properties[prop] as? String?

dependencies {
    implementation("org.spigotmc:spigot-api:${rootProject p "spigot_version"}")
    implementation("com.comphenix.protocol:ProtocolLib:${rootProject p "protocollib_version"}")

    compileApi(project(":emotesAPI")) {
        isTransitive = true
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileApi(project(":executor")) { isTransitive = false }
    compileApi(project(":emotesServer")) { isTransitive = false }
    compileModule(project(":emotesAssets")) { isTransitive = false }
}

tasks.processResources {

    inputs.property("version", project.version)
    inputs.property("description", rootProject p "mod_description")

    filesMatching("plugin.yml"){
        expand("version" to project.version, "description" to (rootProject p "mod_description"))
    }

}


tasks.register("headJar", Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("bukkitHead")
    from(sourceSets.main.get().output)
}

tasks.jar {
    dependsOn(":emotesAPI:jar")
    from(compileModule.map {
        if (it.isDirectory) return@map it else return@map zipTree(it)
    })
    from(compileApi.map {
        if (it.isDirectory) return@map it else return@map zipTree(it)
    })
    archiveClassifier.set("bukkit")
}

tasks.register("copyArtifacts"){
    dependsOn("build")
    doLast {
        copy{
            from("${project.layout.buildDirectory}/libs/${base.archivesName}-${rootProject.mod_version}-bukkit.jar")
            into("${rootProject.projectDir}/artifacts")
        }
    }
}

java {
    withSourcesJar()
}

fun Properties.asStrMap(): HashMap<String, String?> {
    val r = HashMap<String, String?>(size)
    for (e in entries) r[e.key.toString()] = e.value as? String
    return r
}

val Project.keys: Map<String, String?>
    get() = (rootProject.extra.get("keys")!! as Properties).asStrMap()

val Project.keysExists: Boolean
    get() = rootProject.extra.get("keysExists")!! as Boolean

@Suppress("PropertyName")
val Project.mod_version: String
    get() = rootProject.extra.get("mod_version")!! as String


fun addDeps(dependenciesNode: Node, configuration: Configuration, scope: String) {
    val set = configuration.dependencies

    for (dep in set) {
        var group = dep.group
        var artifactId = dep.name
        if (dep is DefaultProjectDependency) {
            val project = project(dep.path)
            for (pub in project.publishing.publications) {
                if (pub !is MavenPublication) continue
                group = pub.groupId
                artifactId = pub.artifactId
                break
            }
        }
        val node = dependenciesNode.appendNode("dependency")
        node.appendNode("groupId", group)
        node.appendNode("artifactId", artifactId)
        node.appendNode("version", dep.version)
        node.appendNode("scope", scope)
        val exclude = dep is ModuleDependency && !dep.isTransitive
        if (exclude) {
            val exclusions = node.appendNode("exclusions")
            val exclusion = exclusions.appendNode("exclusion")
            exclusion.appendNode("groupId", "*")
            exclusion.appendNode("artifactId", "*")
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
//            from(components["java"]) // Script compilation error idk

            artifactId = "emotesBukkit"

            // jar only with classes from this module, dependencies will be included in pom
            artifact(tasks.getByName("headJar")) {
                classifier = ""
            }
            artifact(tasks.sourcesJar)

            pom.withXml {
                val d = asNode().appendNode("dependencies")
                addDeps(d, compileApi, "compile")
                addDeps(d, configurations.implementation.get(), "runtime")
            }


            pom {
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

                licenses {
                    license {
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
            maven("https://maven.kosmx.dev/") {
                credentials {
                    username = "kosmx"
                    password = project.keys["kosmx_maven"]
                }
            }
        } else {
            mavenLocal()
        }
    }
}

if (keysExists) {
    modrinth {
        versionType = rootProject.extra["cfType"]!! as String
        uploadFile = tasks.jar.get().outputs

        token = project.keys["modrinth_token"]
        projectId = "pZ2wrerK"
        versionNumber = "${project.mod_version}+${project p "minecraft_version"}-bukkit"
        versionName = project.mod_version

        gameVersions = listOf("1.21.1")
        changelog = rootProject.extra.get("changes")!! as String
        loaders = listOf("bukkit", "folia", "paper", "purpur", "spigot")
        failSilently = false
    }
}
