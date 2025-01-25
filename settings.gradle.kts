pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
}

rootProject.name = "emotecraft"

include("emotesAPI")

//include "oldJunk"
include("executor")
include("emotesServer")
include("emotesAssets")
include("emotesMc")

//Minecraft 1.20 version
include("minecraft")
include("minecraft:archCommon")
include("minecraft:fabric")
include("minecraft:neoforge")

//Bukkit plugin stuff
include("bukkit")

//Velocity plugin stuff
// include "velocity"
