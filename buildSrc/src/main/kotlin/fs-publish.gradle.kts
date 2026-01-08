import space.sunqian.fs.build.gradle.data.ProjectInfo

plugins {
  `maven-publish`
  signing
}

project.afterEvaluate {

  //val publishType: String? = project.findProperty("publishType")?.toString()
  val publishType: String by extra
  println("publishType: $publishType")

  publishing {
    publications {
      create<MavenPublication>("main") {
        if (publishType == "jar") {
          from(components["java"])
        } else {
          from(components["javaPlatform"])
        }
        val projectInfo: ProjectInfo by rootProject.extra
        pom {
          version = projectInfo.version
          group = rootProject.group
          name = project.name
          description = project.description
          url = projectInfo.url
          licenses {
            projectInfo.licenses.forEach {
              license {
                name.set(it.name)
                url.set(it.url)
              }
            }
          }
          developers {
            projectInfo.developers.forEach {
              developer {
                id.set(it.id)
                name.set(it.name)
                email.set(it.email)
                url.set(it.url)
              }
            }
          }
          scm {
            connection = projectInfo.scm.connection
            developerConnection = projectInfo.scm.developerConnection
            url = projectInfo.scm.url
          }
        }
      }
    }
    repositories {
      // publishToMavenLocal` does not create checksum files in $USER_HOME/.m2/repository. If you want to verify that the
      // checksum files are created correctly, or use them for later publishing, consider configuring a custom Maven
      // repository with a file:// URL and using that as the publishing target instead.
      mavenLocal()
      // maven {
      //   url = uri(file("${System.getProperty("user.home")}/.m2/repository"))
      // }
      //    maven {
      //      url = uri(
      //        if (version.toString().endsWith("SNAPSHOT")) {
      //          "https://your-nexus-url/repository/maven-snapshots/"
      //        } else {
      //          "https://your-nexus-url/repository/maven-releases/"
      //        }
      //      )
      //      credentials {
      //        username = project.findProperty("nexus.username") as String? ?: System.getenv("NEXUS_USERNAME")
      //        password = project.findProperty("nexus.password") as String? ?: System.getenv("NEXUS_PASSWORD")
      //      }
      //    }
    }
  }

  signing {
    //sign(publishing.publications["main"])
    //useGpgCmd()
    // useInMemoryPgpKeys(
    //     project.findProperty("gpg.secretKey") as String?,
    //     project.findProperty("gpg.password") as String?
    // )
  }
}