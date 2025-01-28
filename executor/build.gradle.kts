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