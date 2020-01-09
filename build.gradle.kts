/*
    Gradle configuration is derived from WorldEdit project,
    which is also aiming to support all major Minecraft modding
    platforms like Bukkit, Forge, Sponge and Fabric.

    Additionally, their configuration offers smart library managing and
    shadowing and also some nice stuff like Artifactory support.

    You can check out their skill in Gradle here:
    https://github.com/EngineHub/WorldEdit
    (licensed under LGPLv3)
 */

import org.ajoberstar.grgit.Grgit

applyRootArtifactoryConfig()

if (!project.hasProperty("gitCommitHash")) {
    apply(plugin = "org.ajoberstar.grgit")
    ext["gitCommitHash"] =
            try {
                (ext["grgit"] as Grgit?)?.head()?.abbreviatedId
            } catch (e: Exception) {
                logger.warn("Error getting commit hash", e)

                "no_git_id"
            }
}
