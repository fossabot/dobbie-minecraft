import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

fun Project.applyCommonConfiguration() {
    group = rootProject.group
    version = rootProject.version

    ext["projectName"] = project.rootProject.property("project-name") as String

    repositories {
        mavenCentral()
        maven {
            name = "JFrog Artifactory OSS"
            url = uri("https://oss.jfrog.org/artifactory/libs-release")
        }
    }

    configurations.all {
        resolutionStrategy {
            cacheChangingModulesFor(5, "minutes")
        }
    }
}