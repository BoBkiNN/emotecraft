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
    implementation("com.google.code.gson:gson:2.11.0")
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
        if (project.shouldPublishMaven) {
            kosmxRepo(project)
        } else {
            mavenLocal()
        }
    }
}