plugins {
    `maven-publish`
}

version = rootProject.mod_version

tasks.withType<Copy> {
    eachFile {
        rename {
            it.lowercase()
        }
        //Rename every file to lowercase. This is essential for the translations to work
        //Possibly creates other problems on other operating systems
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "emotesMain"
            from(components["java"])
            withCustomPom("emotesMain", "Minecraft Emotecraft Assets")
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