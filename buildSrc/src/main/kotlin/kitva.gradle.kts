apply(plugin = "kitva.build.pLogger")

project.repositories {
  mavenLocal()
  maven {
    val xMavenRepo: String by project
    url = uri(xMavenRepo)
  }
  mavenCentral()
  //jcenter()
}



