plugins {
    id("java-library")
    id("io.freefair.lombok")
}

applyPlatformAndCoreConfiguration()

dependencies {
    "compile"(project(":libs:core"))

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    "compile"("org.apache.commons:commons-lang3:3.9")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    "compile"("com.google.guava:guava:21.0")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    "compile"("org.slf4j:slf4j-simple:1.7.28")
}

configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:21.0")
    }
}

//sourceSets {
//    main {
//        java {
//            srcDir("src/main/java")
//        }
//        resources {
//            srcDir("src/main/resources")
//        }
//    }
//}

//val crowdinApiKey = "crowdin_apikey"
//
//if (project.hasProperty(crowdinApiKey)) {
//    tasks.named<UploadSourceFileTask>("crowdinUpload") {
//        apiKey = "${project.property(crowdinApiKey)}"
//        projectId = "worldedit-core"
//        files = arrayOf(
//                object {
//                    var name = "strings.json"
//                    var source = "${file("src/main/resources/lang/strings.json")}"
//                }
//        )
//    }
//
//    tasks.named<DownloadTranslationsTask>("crowdinDownload") {
//        apiKey = "${project.property(crowdinApiKey)}"
//        destination = "${file("build/resources/main/lang")}"
//        projectId = "worldedit-core"
//    }
//
//    tasks.named("classes").configure {
//        dependsOn("crowdinDownload")
//    }
//}