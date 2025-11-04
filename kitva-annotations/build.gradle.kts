plugins {
  `java-library`
  jacoco
  `maven-publish`
  signing
  id("kitva")
  id("kitva-publish")
}

description = "Collection of annotations supporting static analyses and providing partial of jsr305."

dependencies {
  //implementation platform(project(":kitva-dependencies"))
  //implementation("com.google.code.findbugs:jsr305")
  testImplementation(platform(project(":kitva-dependencies")))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
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
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
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
      //artifact(tasks["jar"]) {
      //  classifier = "jar"
      //}
      //artifact(tasks["sourcesJar"]) {
      //  classifier = "sources"
      //}
      //artifact(tasks["javadocJar"]) {
      //  classifier = "javadoc"
      //}
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

  // 配置发布目标仓库
  repositories {
    // publishToMavenLocal` does not create checksum files in $USER_HOME/.m2/repository. If you want to verify that the
    // checksum files are created correctly, or use them for later publishing, consider configuring a custom Maven
    // repository with a file:// URL and using that as the publishing target instead.
    mavenLocal()
    maven {
      url = uri(file("${System.getProperty("user.home")}/.m2/repository"))
    }

    // 2. 远程仓库（如Nexus/Ossrh，按需启用）
    //    maven {
    //      // 仓库URL（release和snapshot仓库通常分开）
    //      url = uri(
    //        if (version.toString().endsWith("SNAPSHOT")) {
    //          "https://your-nexus-url/repository/maven-snapshots/"
    //        } else {
    //          "https://your-nexus-url/repository/maven-releases/"
    //        }
    //      )
    //
    //      // 认证信息（建议从gradle.properties或环境变量获取）
    //      credentials {
    //        username = project.findProperty("nexus.username") as String? ?: System.getenv("NEXUS_USERNAME")
    //        password = project.findProperty("nexus.password") as String? ?: System.getenv("NEXUS_PASSWORD")
    //      }
    //    }
  }
}

signing {
  // 对mavenJava发布的所有产物签名
  //sign(publishing.publications["main"])

  // GPG密钥配置（建议通过环境变量或gradle.properties传递，避免硬编码）
  //useGpgCmd() // 使用系统GPG命令（需本地安装GPG）
  // 或使用密钥文件：
  // useInMemoryPgpKeys(
  //     project.findProperty("gpg.secretKey") as String?,
  //     project.findProperty("gpg.password") as String?
  // )
}