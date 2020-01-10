import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
}

applyPlatformAndCoreConfiguration()
applyShadowConfiguration()

repositories {
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public") }
}

configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:21.0")
    }
}

dependencies {
    "api"(project(":core"))
    "implementation"(project(":minecraft:common"))
    "api"(project(":libs:bukkit"))

    "api"("org.spigotmc:spigot-api:1.15.1-R0.1-SNAPSHOT") {
        exclude("junit", "junit")
    }

    "implementation"("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
}

tasks.named<Copy>("processResources") {
    filesMatching("plugin.yml") {
        expand("internalVersion" to project.ext["internalVersion"])
    }
//    from(zipTree("src/main/resources/worldedit-adapters.jar").matching {
//        exclude("META-INF/")
//    })
//    exclude("**/worldedit-adapters.jar")
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        relocate("org.slf4j", (project.group as String) + ".slf4j")
        relocate("org.apache.logging.slf4j", (project.group as String) + ".log4jbridge")
//        relocate("org.antlr.v4", "com.sk89q.worldedit.antlr4")
        include(dependency(":core"))
        include(dependency("org.slf4j:slf4j-api"))
        include(dependency("org.apache.logging.log4j:log4j-slf4j-impl"))
//        include(dependency("org.antlr:antlr4-runtime"))
//        relocate("org.bstats", "com.sk89q.worldedit.bukkit.bstats") {
//            include(dependency("org.bstats:bstats-bukkit:1.5"))
//        }
//        relocate("io.papermc.lib", "com.sk89q.worldedit.bukkit.paperlib") {
//            include(dependency("io.papermc:paperlib:1.0.2"))
//        }
//        relocate("it.unimi.dsi.fastutil", "com.sk89q.worldedit.bukkit.fastutil") {
//            include(dependency("it.unimi.dsi:fastutil"))
//        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

tasks.register<Delete>("cleanupPluginsJar") {
    delete(fileTree("${projectDir}/server/run/plugins") {
        include { file -> file.name.startsWith("temp-") }
    })
}
tasks.named("clean") {
    dependsOn("cleanupPluginsJar")
}

tasks.register<Copy>("copyPluginJar") {
    dependsOn("cleanupPluginsJar", "shadowJar")
    from(tasks.named<ShadowJar>("shadowJar"))
    into("${projectDir}/server/run/plugins")
    rename { name -> "temp-$name" }
}

tasks.register<JavaExec>("startServer") {
    dependsOn("copyPluginJar")
    doFirst {
        mkdir("${projectDir}/server/run")
    }
    defaultCharacterEncoding = "UTF-8"
    maxHeapSize = "1024M"
    classpath = files("${projectDir}/server/bukkit.jar")
    jvmArgs = listOf("-Dcom.mojang.eula.agree=true")
    args = listOf("--noconsole")
    workingDir = file("${projectDir}/server/run")
}