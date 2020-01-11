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
import org.gradle.plugins.ide.idea.model.IdeaModel

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

    var internalVersion = "$version"
    if (internalVersion.contains('+')) {
        internalVersion += '_'
    } else {
        internalVersion += '+'
    }
    ext["internalVersion"] = "$internalVersion${rootProject.ext["gitCommitHash"]}"

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

    if (name == "core" || name == "bukkit") {
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

    val idea = extensions.getByType(IdeaModel::class.java)
    tasks.named("idea") {
        idea.module.outputDir = buildDir.resolve("classes/java/main")
        idea.module.testOutputDir = buildDir.resolve("classes/java/test")
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
        archiveBaseName.set("${ext["projectName"]}-${project.name}")
        archiveClassifier.set("dist")
        dependencies {
            include(project(":libs:core"))
            include(project(":libs:${project.name}"))
            include(project(":libs:minecraft-common"))
            include(project(":minecraft:common"))
            include(project(":core"))
        }
        exclude("GradleStart**")
        exclude(".cache")
        exclude("LICENSE.txt")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/maven/**")
        exclude("META-INF/maven/**")
        exclude("META-INF/proguard/**")
        exclude("opencsv_de.properties")
        exclude("opencsv_en.properties")
        exclude("opencsv_fr.properties")
        exclude("opencsv_pt_BR.properties")
        exclude("mustMatchRegex_de.properties")
        exclude("mustMatchRegex_en.properties")
        exclude("mustMatchRegex_fr.properties")
        exclude("mustMatchRegex_pt_BR.properties")
        exclude("convertGermanToBoolean_de.properties")
        exclude("convertGermanToBoolean_en.properties")
        exclude("convertGermanToBoolean_fr.properties")
        exclude("convertGermanToBoolean_pt_BR.properties")
        exclude("sampleapp.properties")
        //minimize()
    }
}