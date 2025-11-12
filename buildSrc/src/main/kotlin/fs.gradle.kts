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

val javaCompatibleVersion: String by project
val javaCurrentVersion: String by project
val javaCompatibleLang by extra {
  JavaLanguageVersion.of(javaCompatibleVersion)
}
val javaCurrentLang by extra {
  JavaLanguageVersion.of(javaCurrentVersion)
}