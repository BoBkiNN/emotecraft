plugins {
    id 'java'
    id 'maven-publish'
    id 'java-library'
}

version = rootProject.mod_version

repositories {
    mavenCentral()
}

configurations {
    dev
}

dependencies {
    api(project(':emotesAPI'))
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

            artifactId = 'emotesExecutor'

            artifact(jar)
            artifact(sourcesJar)
            artifact(javadocJar)

            pom.withXml {
                def depsNode = asNode().appendNode("dependencies")

                def apiNode = depsNode.appendNode("dependency")
                apiNode.appendNode("groupId", project.group)
                apiNode.appendNode("artifactId", "emotesAPI")
                apiNode.appendNode("version", project.version)
                apiNode.appendNode("scope", "compile")
            }

            pom{
                name = "executor"
                description = "Minecraft Emotecraft executor interface"
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