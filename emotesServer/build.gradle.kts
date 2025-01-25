plugins {
    id 'java'
    id 'java-library'
    id 'maven-publish'
}

version = rootProject.mod_version

repositories {
    mavenCentral()
}

configurations {
    dev
}

dependencies {
    api(project(':executor'))
}

artifacts {
    dev(jar)
}

java {
    withSourcesJar()
    withJavadocJar()
}


publishing {
    publications {
        mavenJava(MavenPublication) {
            // add all the jars that should be included when publishing to maven

            artifactId = 'emotesServer'

            artifact(jar)
            artifact(sourcesJar)
            artifact(javadocJar)

            pom.withXml {
                def depsNode = asNode().appendNode("dependencies")

                def executorNode = depsNode.appendNode("dependency")
                executorNode.appendNode("groupId", project.group)
                executorNode.appendNode("artifactId", "emotesExecutor")
                executorNode.appendNode("version", project.version)
                executorNode.appendNode("scope", "compile")
            }

            pom {
                name = "emotesServer"
                description = "Minecraft Emotecraft server common module"
                url = 'https://github.com/KosmX/emotes'
                developers {
                    developer {
                        id = 'kosmx'
                        name = 'KosmX'
                        email = 'kosmx.mc@gmail.com'
                    }
                }

                licenses{
                    license{
                        name = "CC-BY-4.0 License"
                        url = "https://creativecommons.org/licenses/by/4.0/legalcode"
                    }
                }

                scm {
                    connection = 'scm:git:github.com/kosmx/emotes.git'
                    developerConnection = 'scm:git:github.com/kosmx/emotes.git'
                    url = 'https://github.com/KosmX/emotes'
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
                    url = 'https://maven.kosmx.dev/'
                    credentials {
                        username = 'kosmx'
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