package space.sunqian.fs.build.gradle

/**
 * Project info of this lib.
 */
data class ProjectInfo(
    val version: String,
    val url: String,
    val inceptionYear: String,
    val licenses: List<License>,
    val developers: List<Developer>,
    val scm: Scm,
)

/**
 * License of this lib.
 */
data class License(
    val name: String,
    val url: String,
)

/**
 * Developer of this lib.
 */
data class Developer(
    val id: String,
    val name: String,
    val email: String,
    val url: String,
)

/**
 * Scm of this lib.
 */
data class Scm(
    val connection: String,
    val developerConnection: String,
    val url: String,
)

/**
 * Publish info of this lib.
 */
data class PublishInfo(
    val isEnable: Boolean,
    val isSnapshot: Boolean,
    val isToRemote: Boolean,
    val snapshotId: String,
    val releaseId: String,
    val isSigning: Boolean,
    val signingId: String,
    val snapshotUrl: String,
    val releaseUrl: String,
)