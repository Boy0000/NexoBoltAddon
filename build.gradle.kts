import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    id("java")
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.autoversion)
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    //id("io.papermc.paperweight.userdev") version "2.0.0-beta.8"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") // Paper
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    compileOnly("com.nexomc:nexo:0.7.0")
    compileOnly("org.popcraft:bolt-common:1.1.31")
    compileOnly("org.popcraft:bolt-bukkit:1.1.31")
}

tasks.build {
    dependsOn("shadowJar")
}

copyJar {
    destPath.set(project.findProperty("nexo_plugin_path").toString())
    jarName.set(jarName.orNull ?: "${project.name}-${project.version}-${System.currentTimeMillis()}.jar")
    if ("dev" in jarName.get() && destPath.orNull != null) File(destPath.get()).listFiles { file -> file.extension == "jar" }?.forEach {
        if (jarName.get().startsWith(it.name.substringBefore("-"))) it.delete()
    }
}

bukkitPluginYaml {
    main = "com.nexomc.nexo_bolt_addon.NexoBoltAddon"
    name = "NexoBoltAddon"
    apiVersion = "1.20"
    val version: String by project
    this.version = version
    authors.add("boy0000")
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD

    depend = listOf("Nexo", "Bolt")
}
