import net.labymod.labygradle.common.extension.model.labymod.ReleaseChannels

plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "net.crazy"
version = "1.5.0"

labyMod {
    defaultPackageName = "net.crazy.badges"

    addonInfo {
        namespace = "badges"
        displayName = "Badges"
        author = "Crazy"
        description = "Displays players' laby.net badges above their nametag"
        minecraftVersion = "*"
        version = rootProject.version.toString()

        releaseChannel.set(ReleaseChannels.PRODUCTION)
    }

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    devLogin = false
                }
            }
        }
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}