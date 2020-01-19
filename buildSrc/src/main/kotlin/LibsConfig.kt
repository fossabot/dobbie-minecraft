import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.MavenRepositoryHandlerConvention
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getPlugin
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register

fun Project.applyLibrariesConfiguration() {
    applyCommonConfiguration()
    apply(plugin = "java-base")
    apply(plugin = "maven")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "com.jfrog.artifactory")

    configurations {
        create("shade")
        getByName("archives").extendsFrom(getByName("default"))
    }

    group = "${rootProject.group}.${ext["projectName"]}-libs"

    val libRelocations = mapOf(
            "net.kyori.text" to "util.formatting.text",
            "com.ibm.icu" to "icu"
    )

    tasks.register<ShadowJar>("jar") {
        configurations = listOf(project.configurations["shade"])
        archiveClassifier.set("")

        dependencies {
            exclude(dependency("com.google.guava:guava"))
            exclude(dependency("com.google.code.gson:gson"))
            exclude(dependency("org.checkerframework:checker-qual"))
            exclude(dependency("org.slf4j:slf4j-api"))
        }


        libRelocations.forEach {
            relocate(it.key, (rootProject.group as String) + "." + it.value)
        }
        /*relocate("io", (rootProject.group as String) + ".relocate.io")
        relocate("com", (rootProject.group as String) + ".relocate.com") {
            exclude("com.google.code.**")
            exclude("com.google.guava.**")
        }
        relocate("net", (rootProject.group as String) + ".relocate.net")
        relocate("org", (rootProject.group as String) + ".relocate.org") {
            exclude("org.apache.commons.io.**")
            exclude("org.w3c.**")
        }
        relocate("okhttp3", (rootProject.group as String) + ".relocate.okttp3")
        relocate("okio", (rootProject.group as String) + ".relocate.okio")
        relocate("reactor", (rootProject.group as String) + ".relocate.reactor")
        relocate("rx", (rootProject.group as String) + ".relocate.rx")
        relocate("edu", (rootProject.group as String) + ".relocate.edu")
        relocate("feign", (rootProject.group as String) + ".relocate.feign")*/

    }
    val altConfigFiles = { artifactType: String ->
        val deps = configurations["shade"].incoming.dependencies
                .filterIsInstance<ModuleDependency>()
                .map { it.copy() }
                .map { dependency ->
                    dependency.artifact {
                        name = dependency.name
                        type = artifactType
                        extension = "jar"
                        classifier = artifactType
                    }
                    dependency
                }

        files(configurations.detachedConfiguration(*deps.toTypedArray())
                .resolvedConfiguration.lenientConfiguration.artifacts
                .filter { it.classifier == artifactType }
                .map { zipTree(it.file) })
    }
    tasks.register<Jar>("sourcesJar") {
        from({
            altConfigFiles("sources")
        })

        eachFile {
            filter { f ->
                var rf = f
                libRelocations.forEach { e ->
                    val textPattern = Regex(e.key.replace(".", "\\."))
                    rf = rf.replaceFirst(textPattern, (rootProject.group as String) + "." + e.value)
                }
                rf
            }
            var rp = path
            libRelocations.forEach { e ->
                val filePattern = Regex("(.*)" + e.key.replace('.', '/') + "((?:/|$).*)")
                rp = rp.replaceFirst(filePattern, "$1${(rootProject.group as String).replace(".", "/")}/${e.value.replace('.', '/')}$2")
            }
            path = rp
        }
        archiveClassifier.set("sources")
    }

    tasks.named("assemble").configure {
        dependsOn("jar", "sourcesJar")
    }

    artifacts {
        val jar = tasks.named("jar")
        add("default", jar) {
            builtBy(jar)
        }
        val sourcesJar = tasks.named("sourcesJar")
        add("archives", sourcesJar) {
            builtBy(sourcesJar)
        }
    }

    tasks.register<Upload>("install") {
        configuration = configurations["archives"]
        (repositories as HasConvention).convention.getPlugin<MavenRepositoryHandlerConvention>().mavenInstaller {
            pom.version = project.version.toString()
            pom.artifactId = project.name
        }
    }

    applyCommonArtifactoryConfig()
}