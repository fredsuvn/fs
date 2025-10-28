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