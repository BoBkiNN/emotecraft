plugins {
    java
    `java-library`
    `maven-publish`
}

version = rootProject.mod_version

dependencies {
    api(project(":emotesAPI"))
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "emotesExecutor"

            from(components["java"])

            withCustomPom("executor", "Minecraft Emotecraft executor interface")
        }
    }

    repositories {
        if (project.shouldPublishMaven) {
            kosmxRepo(project)
        } else {
            mavenLocal()
        }
    }
}