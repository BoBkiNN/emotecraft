plugins {
    java
    `java-library`
    `maven-publish`
}

version = rootProject.mod_version

repositories {
    mavenCentral()
}


configurations.register("dev")

dependencies {
    api(project(":executor"))
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
            artifactId = "emotesServer"

            from(components["java"])

            withCustomPom("emotesServer", "Minecraft Emotecraft server common module")
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