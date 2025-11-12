pluginManagement {

  repositories {
    mavenLocal()
    maven {
      val xGradlePluginRepo: String by settings
      url = uri(xGradlePluginRepo)
    }
    mavenCentral()
    gradlePluginPortal()
  }

  plugins {
    val protobufPluginVersion: String by settings
    id("com.google.protobuf") version protobufPluginVersion
    //val spotbugsPluginVersion: String by settings
    //id("com.github.spotbugs") version spotbugsPluginVersion
  }
}

rootProject.name = "fs"

file(rootDir).listFiles()
  ?.filter { it.isDirectory }
  ?.filter { it.name.matches(Regex("fs-(?!(recycle|draft)).*")) }
  ?.forEach { include(":${it.name}") }
// include ":docs"
// include ":tests"