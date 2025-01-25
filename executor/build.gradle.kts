plugins {
    java
    `maven-publish`
    `java-library`
}

version = rootProject.mod_version

repositories {
    mavenCentral()
}

configurations.register("dev")

dependencies {
    api(project(":emotesAPI"))
}

artifacts {
    add("dev", tasks.jar)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "emotesExecutor"

            from(components["java"])

            pom{
                name = "executor"
                description = "Minecraft Emotecraft executor interface"
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

    repositories {
        if (project.keysExists) {
            kosmxRepo(project)
        } else {
            mavenLocal()
        }
    }
}