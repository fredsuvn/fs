import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

fun newProject(): Project {
    val project = ProjectBuilder.builder().build()
    project.extensions.add("pluginLogLevel", "INFO")
    project.extensions.add("xGradlePluginRepo", "https://maven.aliyun.com/repository/gradle-plugin")
    project.extensions.add("xMavenRepo", "https://maven.aliyun.com/repository/public")
    return project;
}