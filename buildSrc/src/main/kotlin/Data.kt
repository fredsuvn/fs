data class ProjectInfo(
    val version: String,
    val group: String,
    val url: String,
    val inceptionYear: String,
    val licenses: List<License>,
    val developers: List<Developer>
)

data class License(
    val name: String,
    val url: String
)

data class Developer(
    val id: String,
    val name: String,
    val email: String,
    val url: String
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
    val releaseUrl: String
)