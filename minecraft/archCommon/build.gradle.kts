architectury {
    common("fabric", "neoforge")
}

loom {
    accessWidenerPath = file("src/main/resources/emotes.accesswidener")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.loader_version}")
    implementation(project(":emotesAssets"))
    implementation(project(path = ":emotesMc", configuration = "namedElements")) {isTransitive = false}
    compileOnly(project(":emotesServer"))
    api(project(":emotesServer"))


    modApi("dev.kosmx.player-anim:player-animation-lib:${rootProject.player_animator_version}")
    modImplementation("dev.kosmx.player-anim:anim-core:${rootProject.player_animator_version}")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "archCommon"

            artifact(tasks.jar) {
                classifier = ""
            }
            artifact(tasks.sourcesJar)

            addDeps(project, configurations.api.get(), "compile")
            addDeps(project, configurations.modApi.get(), "compile")

            withCustomPom("archCommon", "Minecraft Emotecraft Architectury common module")
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
