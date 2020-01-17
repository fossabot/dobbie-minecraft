plugins {
    `java-library`
}

applyPlatformAndCoreConfiguration()

dependencies {
    // https://mvnrepository.com/artifact/com.ibm.icu/icu4j
    "api"("com.ibm.icu:icu4j:65.1")
}