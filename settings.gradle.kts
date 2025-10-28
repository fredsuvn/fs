pluginManagement {
  repositories {
    maven {
      val xGradlePluginRepo: String by settings
      url = uri(xGradlePluginRepo)
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "KitVa"

file(rootDir).listFiles()
  ?.filter { it.isDirectory }
  ?.filter { it.name.matches(Regex("kitva-(?!(recycle|draft)).*")) }
  ?.forEach { include(":${it.name}") }
// include ":docs"
// include ":tests"