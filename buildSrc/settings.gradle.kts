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