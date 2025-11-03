pluginManagement {
  File(rootDir.parentFile, "gradle.properties")
    .inputStream().use { input ->
      val properties = java.util.Properties()
      properties.load(input)
      properties.forEach { (key, value) ->
        settings.extra.set(key.toString(), value)
      }
      gradle.rootProject {
        properties.forEach { (key, value) ->
          this.extra.set(key.toString(), value)
        }
      }
    }
  repositories {
    mavenLocal()
    maven {
      val xGradlePluginRepo: String by settings
      url = uri(xGradlePluginRepo)
    }
    mavenCentral()
    gradlePluginPortal()
  }
}