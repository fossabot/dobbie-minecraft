applyLibrariesConfiguration()

dependencies {
    "shade"("net.kyori:text-api:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-gson:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-legacy:${Versions.TEXT}")
    "shade"("net.kyori:text-serializer-plain:${Versions.TEXT}")
//    "shade"("com.sk89q:jchronic:0.2.4a") {
//        exclude(group = "junit", module = "junit")
//    }
}