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

val javaVersionFrom: String by project
val javaVersionTo: String by project
val javaLangVersionFrom: JavaLanguageVersion by extra {
  JavaLanguageVersion.of(javaVersionFrom)
}
val javaLangVersionTo: JavaLanguageVersion by extra {
  JavaLanguageVersion.of(javaVersionTo)
}