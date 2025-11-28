apply(plugin = "fs.build.pLogger")

project.repositories {
  mavenLocal()
  maven {
    val xMavenRepo: String by project
    url = uri(xMavenRepo)
  }
  mavenCentral()
  //jcenter()
}

val javaVersionLow: String by project
val javaVersionHigh: String by project
val javaLanguageVersionLow by extra {
  JavaLanguageVersion.of(javaVersionLow)
}
val javaLanguageVersionHigh by extra {
  JavaLanguageVersion.of(javaVersionHigh)
}