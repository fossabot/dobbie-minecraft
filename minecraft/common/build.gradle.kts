plugins {
    id("java-library")
    id("io.freefair.lombok")
}

applyPlatformAndCoreConfiguration()

dependencies {
    "api"(project(":dobbie-core"))
    "api"(project(":libs:minecraft-common"))
}