import java.util.*

plugins {
  id("kitva")
  id("maven-publish")
  id("signing")
}

val pLogger: PluginLogger by project

//project.afterEvaluate {
//  val projectInfo: ProjectInfo by project
//  val publishInfo: PublishInfo by project
//  configurePublishing(projectInfo, publishInfo)
//}

fun configurePublishing(projectInfo: ProjectInfo, publishInfo: PublishInfo) {
  configure<PublishingExtension> {
    publications {
      create<MavenPublication>("mavenJar") {
        if (components.findByName("javaPlatform") != null) {
          from(components["javaPlatform"])
        } else {
          from(components["java"])
        }
        // artifact(tasks.getByName("sourceJar"))
        // artifact(tasks.getByName("javadocJar"))
        configurePublishMavenPom(this, projectInfo)
      }

      tasks.findByName("javadocJar")?.let { javadocJarTask ->
        create<MavenPublication>("mavenJavadocJar") {
          artifact(javadocJarTask) {
            classifier = "javadoc"
          }
        }
      }

      tasks.findByName("sourcesJar")?.let { sourcesJarTask ->
        create<MavenPublication>("mavenSourcesJar") {
          artifact(sourcesJarTask) {
            classifier = "sources"
          }
        }
      }
    }

    if (publishInfo.isToRemote) {
      repositories {
        configurePublishRepositories(this, publishInfo)
      }
    }

    if (publishInfo.isSigning) {
      configureSigning(publishInfo)
      configure<SigningExtension> {
        sign(publications.getByName("mavenJar"))
      }
    }
  }
}

fun configurePublishMavenPom(maven: MavenPublication, projectInfo: ProjectInfo) {
  maven.pom {
    name.set(project.name)
    description.set(project.description ?: "")
    url.set(projectInfo.url)
    inceptionYear.set(projectInfo.inceptionYear)

    scm {
      connection.set("scm:git:${projectInfo.url}.git")
      developerConnection.set("scm:git:${projectInfo.url}.git")
      url.set(projectInfo.url)
    }

    projectInfo.licenses.forEach { license ->
      licenses {
        license {
          name.set(license.name)
          url.set(license.url)
        }
      }
    }

    projectInfo.developers.forEach { developer ->
      developers {
        developer {
          email.set(developer.email)
        }
      }
    }

    pLogger.debug("maven-pom.name: ${name.get()}")
    pLogger.debug("maven-pom.description: ${description.get()}")
  }
}

fun configurePublishRepositories(repositories: RepositoryHandler, publishInfo: PublishInfo) {
  if (publishInfo.isSnapshot) {
    repositories.maven {
      name = publishInfo.snapshotId
      url = uri(publishInfo.snapshotUrl)
      pLogger.debug("snapshot.name: $name")
      pLogger.debug("snapshot.url: $url")
      credentials {
        username = project.findProperty("publish${name.capitalize()}Username") as? String ?: ""
        password = project.findProperty("publish${name.capitalize()}Password") as? String ?: ""
        pLogger.debug("snapshot.username: $username")
        pLogger.debug("snapshot.password: $password")
      }
    }
  } else {
    repositories.maven {
      name = publishInfo.releaseId
      url = uri(publishInfo.releaseUrl)
      pLogger.debug("release.name: $name")
      pLogger.debug("release.url: $url")
      credentials {
        username = project.findProperty("publish${name.capitalize()}Username") as? String ?: ""
        password = project.findProperty("publish${name.capitalize()}Password") as? String ?: ""
        pLogger.debug("release.username: $username")
        pLogger.debug("release.password: $password")
      }
    }
  }
}

fun configureSigning(publishInfo: PublishInfo) {
  configure<SigningExtension> {
    val signingName = publishInfo.signingId
    val signingKeyId = project.findProperty("signing${signingName.capitalize()}KeyId") as? String ?: ""
    val signingPassword = project.findProperty("signing${signingName.capitalize()}Password") as? String ?: ""
    val signingKeyFile = project.findProperty("signing${signingName.capitalize()}KeyFile") as? String ?: ""
    val signingKey = if (signingKeyFile.isNotEmpty()) {
      file(signingKeyFile).readText(Charsets.UTF_8)
    } else {
      ""
    }

    pLogger.debug("signing.signingKeyId: $signingKeyId")
    pLogger.debug("signing.signingPassword: $signingPassword")
    pLogger.debug("signing.signingKey: $signingKey")

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
  }
}

fun String.capitalize(): String {
  return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}