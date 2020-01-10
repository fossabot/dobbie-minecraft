plugins {
    id("java-library")
    id("io.freefair.lombok")
}

applyPlatformAndCoreConfiguration()

dependencies {
    "api"(project(":core"))
    "api"(project(":libs:minecraft-common"))
    "implementation"("com.google.code.gson:gson:2.8.6")
}