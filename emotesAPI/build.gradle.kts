plugins {
    java
    `java-library`
    `maven-publish`
}

version = rootProject.mod_version

val dev = configurations.register("dev")

dependencies {
    api("dev.kosmx.player-anim:anim-core:${rootProject.player_animator_version}")
    implementation("com.google.code.gson:gson:2.11.0") // gson for MC 1.21.4

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}
tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    sourceCompatibility = "21"
    targetCompatibility = "21"
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
            artifactId = "emotesAPI"

            from(components["java"]) // jar, sourcesJar, javdocJar

            withCustomPom("emotesApi", "Minecraft Emotecraft API")
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