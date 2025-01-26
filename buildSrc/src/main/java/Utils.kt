import groovy.util.Node
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.maven
import java.util.*

fun Properties.asStrMap(): HashMap<String, String?> {
    val r = HashMap<String, String?>(size)
    for (e in entries) r[e.key.toString()] = e.value as? String
    return r
}

fun getGitRevision(): String {
    val p = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--verify", "--short", "HEAD"))
    return p.inputReader().readText().trim()
}

fun XmlProvider.removeDependencies(artifactIds: List<String>) {
    val dependenciesNode = asElement().getElementsByTagName("dependency")
    val dependenciesToRemove = mutableListOf<org.w3c.dom.Node>()

    for (i in 0 until dependenciesNode.length) {
        val dependency = dependenciesNode.item(i)
        val artifactIdNode = dependency.childNodes.let { nodes ->
            (0 until nodes.length)
                .map { nodes.item(it) }
                .find { it.nodeName == "artifactId" }
        }

        if (artifactIdNode != null && artifactIdNode.textContent in artifactIds) {
            dependenciesToRemove.add(dependency)
        }
    }

    dependenciesToRemove.forEach { it.parentNode.removeChild(it) }
}

fun Project.addDeps(dependenciesNode: Node, configuration: Configuration, scope: String) {
    val set = configuration.dependencies
    for (dep in set) {
        var group = dep.group
        var artifactId = dep.name
        if (dep is DefaultProjectDependency) {
            val project = project(dep.path)
            val publishing = project.extensions.findByType(PublishingExtension::class.java)
            if (publishing == null) continue
            for (pub in publishing.publications) {
                if (pub !is MavenPublication) continue
                group = pub.groupId
                artifactId = pub.artifactId
                break
            }
        }
        val node = dependenciesNode.appendNode("dependency")
        node.appendNode("groupId", group)
        node.appendNode("artifactId", artifactId)
        node.appendNode("version", dep.version)
        node.appendNode("scope", scope)
        val exclude = dep is ModuleDependency && !dep.isTransitive
        if (exclude) {
            val exclusions = node.appendNode("exclusions")
            val exclusion = exclusions.appendNode("exclusion")
            exclusion.appendNode("groupId", "*")
            exclusion.appendNode("artifactId", "*")
        }
    }
}

/**
 * Adds a maven.kosmx.dev repository with username and password set from [project]
 */
fun RepositoryHandler.kosmxRepo(project: Project): MavenArtifactRepository {
    return maven("https://maven.kosmx.dev/") {
        credentials {
            username = "kosmx"
            password = project.keys["kosmx_maven"]
        }
    }
}

fun MavenPublication.withCustomPom(name: String, desc: String) {
    pom {
        this.name = name
        description = desc
        url = "https://github.com/KosmX/emotes"
        developers {
            developer {
                id = "kosmx"
                this.name = "KosmX"
                email = "kosmx.mc@gmail.com"
            }
        }

        licenses {
            license{
                this.name = "CC-BY-4.0 License"
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