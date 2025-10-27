pluginManagement {
  repositories {
    maven {
      val myGradlePluginRepo: String by settings
      url = uri(myGradlePluginRepo)
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