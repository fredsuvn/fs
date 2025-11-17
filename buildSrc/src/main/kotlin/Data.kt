data class ProjectInfo(
    val version: String,
    val url: String,
    val inceptionYear: String,
    val licenses: List<License>,
    val developers: List<Developer>,
    val scm: Scm,
)

data class License(
    val name: String,
    val url: String,
)

data class Developer(
    val id: String,
    val name: String,
    val email: String,
    val url: String,
)

data class Scm(
    val connection: String,
    val developerConnection: String,
    val url: String,
)

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