plugins {
  `kotlin-dsl`
  //`java-gradle-plugin`
  //`groovy-gradle-plugin`
}

repositories {
  maven {
    val myGradlePluginRepo: String by project
    url = uri(myGradlePluginRepo)
  }
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  //implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
  testImplementation("org.testng:testng:7.5.1")
}

gradlePlugin {
  plugins {
    create("pLogger") {
      id = "kitva.build.pLogger"
      implementationClass = "PluginLogger"
    }
  }
}