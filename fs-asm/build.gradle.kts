plugins {
  `java-library`
  jacoco
  `maven-publish`
  signing
  id("fs")
}

description = "Internal ASM (7.1) for fs."

dependencies {
  testImplementation(platform(project(":fs-dependencies")))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val javaVersionLow = project.property("javaLanguageVersionLow") as JavaLanguageVersion
val javaVersionHigh = project.property("javaLanguageVersionHigh") as JavaLanguageVersion

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = javaVersionLow
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = javaVersionLow
  }
}

tasks.named<Javadoc>("javadoc") {
  val ops = options as StandardJavadocDocletOptions
  ops.encoding = "UTF-8"
  ops.locale = "en-us"
  ops.charSet = "UTF-8"
  ops.docEncoding = "UTF-8"
  ops.jFlags("-Duser.language=en", "-Duser.country=US")
  ops.addStringOption("Xdoclint:none", "-quiet")
  javadocTool = javaToolchains.javadocToolFor {
    languageVersion = javaVersionHigh
  }
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    html.required = false
    xml.required = false
    csv.required = false
  }
}

publishing {
  publications {
    create<MavenPublication>("main") {
      from(components["java"])
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
    maven {
      url = uri(file("${System.getProperty("user.home")}/.m2/repository"))
    }
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