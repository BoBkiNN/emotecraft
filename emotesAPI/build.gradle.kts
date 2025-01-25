plugins {
    java
    `java-library`
    `maven-publish`
}

version = rootProject.mod_version

//sourceCompatibility = JavaVersion.VERSION_1_8
//targetCompatibility = JavaVersion.VERSION_1_8

val dev = configurations.register("dev")

dependencies {
    api("dev.kosmx.player-anim:anim-core:${rootProject.player_animator_version}")
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}
tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.release.set(21) //Build on JDK 1.8
}

artifacts {
    add("dev", tasks.jar.get())
}

//-------- publishing --------

java {
    withSourcesJar()
    withJavadocJar()
}



publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            // add all the jars that should be included when publishing to maven
            artifactId = "emotesAPI"

            from(components["java"])

            pom {
                name = "emotesApi"
                description = "Minecraft Emotecraft api"
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