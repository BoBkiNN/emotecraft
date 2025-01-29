import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.internal.extensions.core.extra
import java.util.*


val Project.ext
    get() = rootProject.extra

val ENV: Map<String, String> by lazy { System.getenv() }

var Project.isRelease: Boolean
    get() = ext.get("isRelease") as Boolean
    set(v) = ext.set("isRelease", v)

var Project.changes: String
    get() = ext.get("changes") as String
    set(v) = ext.set("changes", v)

val Project.maven_group
    get() = properties["maven_group"] as String

val Project.java_version
    get() = properties["java_version"] as String

var Project.shouldPublishMaven: Boolean
    get() = rootProject.extra.get("shouldPublishMaven")!! as Boolean
    set(v) = rootProject.extra.set("shouldPublishMaven", v)

var Project.mod_version
    get() = rootProject.extra.get("mod_version").toString()
    set(v) {rootProject.extra.set("mod_version", v)}

val Project.version_base
    get() = properties["version_base"] as String

val Project.minecraft_version
    get() = properties["minecraft_version"] as String

val Project.parchment_version
    get() = properties["parchment_version"] as String

val Project.player_animator_version
    get() = properties["player_animator_version"] as String

val Project.mod_description
    get() = properties["mod_description"] as String

val Project.loader_version
    get() = properties["loader_version"] as String

val Project.fabric_api_version
    get() = properties["fabric_api_version"] as String

val Project.modmenu_version
    get() = properties["modmenu_version"] as String

val Project.neoforge_version
    get() = properties["neoforge_version"] as String

/**
 * Can be `stable`, `beta`, `alpha`
 */
var Project.releaseType
    get() = rootProject.extra["releaseType"]!! as String
    set(v) = rootProject.extra.set("releaseType", v)

val Project.archives_base_name
    get() = properties["archives_base_name"] as String
