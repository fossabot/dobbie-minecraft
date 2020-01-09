//import net.minecrell.gradle.licenser.LicenseExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.freefair.gradle.plugins.lombok.LombokExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.CoreJavadocOptions
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

fun Project.applyPlatformAndCoreConfiguration() {
    applyCommonConfiguration()
    apply(plugin = "java")
    apply(plugin = "eclipse")
    apply(plugin = "idea")
    apply(plugin = "maven")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "com.jfrog.artifactory")
//    apply(plugin = "net.minecrell.licenser")
    apply(plugin = "io.freefair.lombok")

    ext["internalVersion"] = "$version;${rootProject.ext["gitCommitHash"]}"

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT}")
        "testImplementation"("org.mockito:mockito-core:${Versions.MOCKITO}")
        "testImplementation"("org.mockito:mockito-junit-jupiter:${Versions.MOCKITO}")
        "testImplementation"("org.mockito:mockito-inline:${Versions.MOCKITO}")
        "testRuntime"("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}")
    }

    // Java 8 turns on doclint which we fail
    tasks.withType<Javadoc>().configureEach {
        (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.register<Jar>("javadocJar") {
        dependsOn("javadoc")
        archiveClassifier.set("javadoc")
        from(tasks.getByName<Javadoc>("javadoc").destinationDir)
    }

    tasks.named("assemble").configure {
        dependsOn("javadocJar")
    }

    artifacts {
        add("archives", tasks.named("jar"))
        add("archives", tasks.named("javadocJar"))
    }

    val projectName = ext["projectName"]
    if (name == "$projectName-core" || name == "$projectName-bukkit") {
        tasks.register<Jar>("sourcesJar") {
            dependsOn("classes")
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }

        artifacts {
            add("archives", tasks.named("sourcesJar"))
        }
        tasks.named("assemble").configure {
            dependsOn("sourcesJar")
        }
    }

    val lombok = extensions.getByType(LombokExtension::class.java)
    tasks.named("generateLombokConfig") {
        lombok.config.put("lombok.anyConstructor.addConstructorProperties", "true")
    }

//    tasks.named("check").configure {
//        dependsOn("checkstyleMain", "checkstyleTest")
//    }

    applyCommonArtifactoryConfig()

//    configure<LicenseExtension> {
//        header = rootProject.file("HEADER.txt")
//        include("**/*.java")
//    }
}

fun Project.applyShadowConfiguration() {
    tasks.named<ShadowJar>("shadowJar") {
        val projectName = project.rootProject.property("project-name") as String
        archiveClassifier.set("dist")
        dependencies {
            include(project(":libs:core"))
            include(project(":libs:${project.name.replace("$projectName-", "")}"))
            include(project(":$projectName-core"))
        }
        exclude("GradleStart**")
        exclude(".cache")
        exclude("LICENSE*")
        exclude("META-INF/maven/**")
        minimize()
    }
}