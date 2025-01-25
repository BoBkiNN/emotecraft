import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.internal.extensions.core.extra
import java.util.*


private fun Properties.asStrMap(): HashMap<String, String?> {
    val r = HashMap<String, String?>(size)
    for (e in entries) r[e.key.toString()] = e.value as? String
    return r
}

val Project.ext
    get() = rootProject.extra

val ENV: Map<String, String>
    get() = System.getenv()

var Project.isRelease: Boolean
    get() = ext.get("isRelease") as Boolean
    set(v) = ext.set("isRelease", v)

var Project.changes: String
    get() = ext.get("changes") as String
    set(v) = ext.set("changes", v)

@Suppress("UNCHECKED_CAST")
val ExtraPropertiesExtension.keys: MutableMap<String, String>
    get() = get("keys") as MutableMap<String, String>

val Project.maven_group
    get() = properties["maven_group"] as String

val Project.java_version
    get() = properties["java_version"] as String

val Project.keys: Map<String, String?>
    get() = (rootProject.extra.get("keys")!! as Properties).asStrMap()
var Project.keysExists: Boolean
    get() = rootProject.extra.get("keysExists")!! as Boolean
    set(v) = rootProject.extra.set("keysExists", v)

var Project.mod_version
    get() = rootProject.extra.get("mod_version").toString()
    set(v) {rootProject.extra.set("mod_version", v)}

val Project.version_base
    get() = properties["version_base"] as String

val Project.minecraft_version
    get() = properties["minecraft_version"] as String

val Project.player_animator_version
    get() = properties["player_animator_version"] as String

val Project.mod_description
    get() = properties["mod_description"] as String

var Project.cfType
    get() = rootProject.extra["cfType"]!! as String
    set(v) = rootProject.extra.set("cfType", v)

fun getGitRevision(): String {
    val p = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--verify", "--short", "HEAD"))
    return p.inputReader().readText().trim()
}