import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import java.util.*


val Project.changes
    get() = rootProject.extra.get("changes")!! as String

private fun Properties.asStrMap(): HashMap<String, String?> {
    val r = HashMap<String, String?>(size)
    for (e in entries) r[e.key.toString()] = e.value as? String
    return r
}

val Project.keys: Map<String, String?>
    get() = (rootProject.extra.get("keys")!! as Properties).asStrMap()
val Project.keysExists: Boolean
    get() = rootProject.extra.get("keysExists")!! as Boolean

val Project.mod_version
    get() = properties["mod_version"].toString()

val Project.minecraft_version
    get() = properties["minecraft_version"] as String

val Project.player_animator_version
    get() = properties["player_animator_version"] as String

val Project.mod_description
    get() = properties["mod_description"] as String

val Project.cfType
    get() = rootProject.extra["cfType"]!! as String