import space.sunqian.fs.build.gradle.data.*

plugins {
  id("fs")
}

description = "Root project of fs."

rootProject.group = "space.sunqian.fs"

val projectVersion: String by project
version = projectVersion

val projectInfo by extra(
  ProjectInfo(
    version = projectVersion,
    url = "https://github.com/fredsuvn/fs",
    inceptionYear = "2020",
    licenses = listOf(
      License(
        name = "The Apache License, Version 2.0",
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
      )
    ),
    developers = listOf(
      Developer(
        id = "fredsuvn",
        name = "孙谦",
        email = "fredsuvn@163.com",
        url = "https://github.com/fredsuvn"
      )
    ),
    scm = Scm(
      connection = "scm:git:https://github.com/fredsuvn/fs.git",
      developerConnection = "scm:git:https://github.com/fredsuvn/fs.git",
      url = "https://github.com/fredsuvn/fs"
    )
  )
)

val publishInfo by extra(
  PublishInfo(
    //Whether enable publish
    isEnable = true,
    //Whether publish as snapshot
    isSnapshot = false,
    /*
     * Whether publish to remote.
     * To enable publish to remote repository, you must configure publishXxxUsername for snapshotId and releaseId.
     */
    isToRemote = false,
    snapshotId = "ossrh",
    releaseId = "ossrh",
    /*
     * Whether signing.
     * To enable signing, you must configure signingXxxKeyId, signingXxxPassword and signingXxxKeyFile for signingId.
     */
    isSigning = false,
    signingId = "gpg",
    snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots",
    releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2",
  )
)

tasks.register("printVersion") {
  group = "version"
  description = "Prints the project version"
  doLast {
    println(project.version)
  }
}