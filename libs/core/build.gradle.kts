applyLibrariesConfiguration()

dependencies {
    // https://twitch4j.gitlab.io/twitch4j/getting-started/installation/
    "shade"("com.github.twitch4j:twitch4j:1.0.0-alpha.17")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    "shade"("com.fasterxml.jackson.core:jackson-core:2.9.10")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    "shade"("com.fasterxml.jackson.core:jackson-databind:2.9.10")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    "shade"("com.fasterxml.jackson.core:jackson-annotations:2.9.10")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml
    "shade"("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.10")
    // https://mvnrepository.com/artifact/com.opencsv/opencsv
    "shade"("com.opencsv:opencsv:5.0")
    // https://mvnrepository.com/artifact/org.mozilla/rhino
    "shade"("org.mozilla:rhino:1.7.11")

    "shade"("net.kyori:text-api:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-gson:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-legacy:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-plain:${Versions.TEXT}")
//    "shade"("com.sk89q:jchronic:0.2.4a") {
//        exclude(group = "junit", module = "junit")
//    }
}